package com.nuo.nuopaoserver.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.dto.FollowPageDto;
import com.nuo.nuopaoserver.entity.UserFollow;
import com.nuo.nuopaoserver.enums.FollowStatus;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.UserFollowMapper;
import com.nuo.nuopaoserver.service.UserFollowService;
import com.nuo.nuopaoserver.vo.FollowPageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    /**
     * 关注用户（不能关注自己；重复关注幂等成功，靠唯一索引兜底并发）
     * @param id 被关注用户id
     */
    @Override
    public void follow(Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new BizException("不能关注自己");
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
    }

    /**
     * 获取当前用户与目标用户的关注关系
     * @param id 目标用户id
     */
    @Override
    public FollowStatus getStatus(Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        boolean iFollowHim = this.lambdaQuery()
                .eq(UserFollow::getUserId, currentUserId)
                .eq(UserFollow::getFollowUserId, id)
                .exists();
        if (!iFollowHim) {
            return FollowStatus.NOT_FOLLOWING;
        }
        boolean heFollowsMe = this.lambdaQuery()
                .eq(UserFollow::getUserId, id)
                .eq(UserFollow::getFollowUserId, currentUserId)
                .exists();
        return heFollowsMe ? FollowStatus.MUTUAL : FollowStatus.FOLLOWING;
    }

    @Override
    public void unfollow(Long id) {
        this.lambdaUpdate()
                .eq(UserFollow::getUserId, UserContext.getCurrentUserId())
                .eq(UserFollow::getFollowUserId, id)
                .remove();
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
