package com.nuo.nuopaoserver.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.constant.RedisConstant;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.dto.FollowPageDto;
import com.nuo.nuopaoserver.entity.UserFollow;
import com.nuo.nuopaoserver.enums.FollowStatus;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.UserFollowMapper;
import com.nuo.nuopaoserver.service.UserFollowService;
import com.nuo.nuopaoserver.vo.FollowPageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    private final StringRedisTemplate redis;
    /**
     * 关注用户（不能关注自己；重复关注幂等成功，DB 唯一索引兜底并发）
     * Redis 同步：DB 成功后写两个方向的 Set，失败只记日志，读时 miss 回源自愈
     * @param id 被关注用户id
     */
    @Override
    public void follow(Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BizException("不能关注自己");
        }
        // 注意 key 归属：USER_FOLLOW+我 的成员是「我关注的人」（对方id），别和 fans 方向搞反
        String followKey = RedisConstant.USER_FOLLOW + currentUserId;

        // Redis 预检查：key 存在才可信（key 不存在≠没关注，可能只是缓存未加载），此时直接放行给 DB 判断
        if (Boolean.TRUE.equals(redis.hasKey(followKey))
                && Boolean.TRUE.equals(redis.opsForSet().isMember(followKey, String.valueOf(id)))) {
            return;
        }
        boolean exists = this.lambdaQuery()
                .eq(UserFollow::getUserId, currentUserId)
                .eq(UserFollow::getFollowUserId, id)
                .exists();
        if (exists) {
            return;
        }
        try {
            UserFollow userFollow = new UserFollow();
            userFollow.setUserId(currentUserId);
            userFollow.setFollowUserId(id);
            this.save(userFollow);
        } catch (DuplicateKeyException e) {
            // 并发下重复关注，唯一索引拦下后视为已关注
            log.info("重复关注被唯一索引拦截，userId={} followUserId={}", currentUserId, id);
        }
        // DB 成功后同步 Redis：两个方向各写一个 Set；失败不回滚 DB，读时 miss 回源自愈
        try {
            redis.opsForSet().add(followKey, String.valueOf(id));
            redis.opsForSet().add(RedisConstant.USER_FANS + id, String.valueOf(currentUserId));
        } catch (Exception e) {
            log.warn("关注关系同步 Redis 失败，userId={} followUserId={}", currentUserId, id, e);
            evictFollowCache(currentUserId, id);
        }
    }

    /**
     * 获取当前用户与目标用户的关注关系（Redis SISMEMBER，miss 回源 DB 重建）
     * 不假设 Redis 数据齐全：key 不存在只代表"未加载"，重建后即可信任
     * @param id 目标用户id
     */
    @Override
    public FollowStatus getStatus(Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        String followKey = RedisConstant.USER_FOLLOW + currentUserId;
        String fansKey = RedisConstant.USER_FANS + currentUserId;
        ensureFollowCache(followKey, currentUserId);
        ensureFollowCache(fansKey, currentUserId);

        boolean iFollowHim = Boolean.TRUE.equals(
                redis.opsForSet().isMember(followKey, String.valueOf(id)));
        if (!iFollowHim) {
            return FollowStatus.NOT_FOLLOWING;
        }
        boolean heFollowsMe = Boolean.TRUE.equals(
                redis.opsForSet().isMember(fansKey, String.valueOf(id)));
        return heFollowsMe ? FollowStatus.MUTUAL : FollowStatus.FOLLOWING;
    }

    /** 关注缓存有效期：到期自动失效回源，兜底"同步失败残留脏数据"的情况 */
    private static final Duration FOLLOW_CACHE_TTL = Duration.ofMinutes(60);
    private static final String EMPTY_SUFFIX = ":empty";

    /**
     * 确保某个方向的关注缓存已加载：key（或其空占位）不存在时从 DB 全量重建
     * @param key    完整缓存 key（user:follow:x 或 user:fans:x）
     * @param userId 归属用户
     */
    private void ensureFollowCache(String key, Long userId) {
        if (Boolean.TRUE.equals(redis.hasKey(key))
                || Boolean.TRUE.equals(redis.hasKey(key + EMPTY_SUFFIX))) {
            return;
        }
        // follow 方向按 userId 列查本人；fans 方向按 followUserId 列查本人
        boolean isFollowKey = key.startsWith(RedisConstant.USER_FOLLOW);
        List<UserFollow> relations = this.lambdaQuery()
                .eq(isFollowKey ? UserFollow::getUserId : UserFollow::getFollowUserId, userId)
                .list();
        if (relations.isEmpty()) {
            // 坑：SADD 空集合不会创建 key，必须写占位，否则每次都回源（缓存击穿）
            redis.opsForValue().set(key + EMPTY_SUFFIX, "1", FOLLOW_CACHE_TTL);
            return;
        }
        // 之前若留过空占位，正式 key 建好后清掉，避免它误判"已加载"
        redis.delete(key + EMPTY_SUFFIX);
        String[] members = relations.stream()
                .map(r -> String.valueOf(isFollowKey ? r.getFollowUserId() : r.getUserId()))
                .toArray(String[]::new);
        redis.opsForSet().add(key, members);
        redis.expire(key, FOLLOW_CACHE_TTL);
    }

    /** Redis 同步失败时删掉受影响的 key，打回"未加载"状态，下次读自动回源，避免残留半截脏数据 */
    private void evictFollowCache(Long userId, Long targetId) {
        try {
            redis.delete(RedisConstant.USER_FOLLOW + userId);
            redis.delete(RedisConstant.USER_FANS + targetId);
        } catch (Exception e) {
            log.warn("删除关注缓存失败，userId={} targetId={}", userId, targetId, e);
        }
    }

    /**
     * 取消关注（DB 先行，成功后同步清两个方向的 Set，Redis 失败只记日志）
     * @param id 被取关用户id
     */
    @Override
    public void unfollow(Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        this.lambdaUpdate()
                .eq(UserFollow::getUserId, currentUserId)
                .eq(UserFollow::getFollowUserId, id)
                .remove();
        try {
            redis.opsForSet().remove(RedisConstant.USER_FOLLOW + currentUserId, String.valueOf(id));
            redis.opsForSet().remove(RedisConstant.USER_FANS + id, String.valueOf(currentUserId));
        } catch (Exception e) {
            log.warn("取关同步 Redis 失败，userId={} followUserId={}", currentUserId, id, e);
            evictFollowCache(currentUserId, id);
        }
    }

    /**
     * 关注列表（分页，联用户表返回昵称头像；dto.userId 为空时默认查自己）
     */
    @Override
    public Page<FollowPageVo> followList(FollowPageDto dto) {
        if (dto.getUserId() == null) {
            dto.setUserId(UserContext.getCurrentUserId());
        }
        return this.baseMapper.followList(new Page<>(dto.getPage(), dto.getPageSize()), dto);
    }
}
