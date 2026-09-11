package com.nuo.nuopaoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.constant.RedisConstant;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.dto.*;
import com.nuo.nuopaoserver.entity.Tag;
import com.nuo.nuopaoserver.entity.User;
import com.nuo.nuopaoserver.entity.UserTag;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.TagMapper;
import com.nuo.nuopaoserver.mapper.UserMapper;
import com.nuo.nuopaoserver.mapper.UserTagMapper;
import com.nuo.nuopaoserver.service.UserService;
import com.nuo.nuopaoserver.vo.GetUserByTagsVo;
import com.nuo.nuopaoserver.vo.LoginVo;
import com.nuo.nuopaoserver.vo.TagVo;
import com.nuo.nuopaoserver.vo.UserDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final StringRedisTemplate redis;
    private final TemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${spring.mail.username}")
    private String from;
    private final TagMapper tagMapper;
    private final UserTagMapper userTagMapper;

    @Override
    public void registerGetCode(@Valid UserRegisterGetCode dto) {
        //检查邮箱是否注册过
        User user = this.getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getEmail, dto.getEmail()));
        if(user != null){
            throw new BizException("该账户已存在");
        }
        //检查邮箱验证码是否已发送
        if (redis.hasKey(RedisConstant.USER_REGISTER_CODE + dto.getEmail())) {
            throw new BizException("验证码已发送，请勿重复获取");
        }
        //生成验证码
        String code = RandomUtil.randomString(6);
        redis.opsForValue().set(RedisConstant.USER_REGISTER_CODE + dto.getEmail(), code, RedisConstant.USER_REGISTER_CODE_EXPIRE, TimeUnit.MINUTES);
        log.info("Sent  code {} to {}", code, dto.getEmail());
        Context context = new Context();
        context.setVariable("username",dto.getEmail());
        context.setVariable("expireMinute",RedisConstant.USER_REGISTER_CODE_EXPIRE);
        context.setVariable("code", code);
        emailService.sendEmail(
                dto.getEmail(),
                context,
                "mail/register",
                "【NuoPao注册验证码】"
        );
    }


    @Override
    public void register(UserRegisterDto dto) {
        String s = redis.opsForValue().get(RedisConstant.USER_REGISTER_CODE + dto.getEmail());
        if(s == null){
            throw new BizException("验证码已过期");
        }
        if(!s.equals(dto.getCode())){
            throw new BizException("验证码错误");
        }
        User user = BeanUtil.copyProperties(dto, User.class);
        Snowflake snowflake = new Snowflake(1, 1);
        long l = snowflake.nextId();
        String planeCode = Base62.encode(String.valueOf(l));
        user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
        user.setPlanetCode(planeCode);
        save(user);
        redis.delete(RedisConstant.USER_REGISTER_CODE + dto.getEmail());
    }

    @Override
    public void loginGetCode(@Valid UserLoginGetCode dto) {
        User user = this.getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getEmail, dto.getEmail()));
        if(user == null){
            throw new BizException("该账户不存在");
        }
        String codeKey = RedisConstant.USER_LOGIN +dto.getEmail();
        if (redis.hasKey(codeKey)) {
            throw new BizException("验证码已发送，请勿重复获取");
        }
        String code = RandomUtil.randomString(6);
        redis.opsForValue().set(codeKey, code, RedisConstant.USER_LOGIN_EXPIRE, TimeUnit.MINUTES);
        log.info("Sent  code {} to {}", code, dto.getEmail());
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        context.setVariable("expireMinute",RedisConstant.USER_LOGIN_EXPIRE);
        context.setVariable("code", code);
        emailService.sendEmail(
                dto.getEmail(),
                context,
                "mail/login",
                "【NuoPao登录验证码】"
        );
    }

    @Override
    public LoginVo emailLogin(EmailLoginDto dto) {
        String key = RedisConstant.USER_LOGIN + dto.getEmail();
        if(!redis.hasKey(key)){
            throw new BizException("验证码已过期");
        }
        String code = redis.opsForValue().get(key);
        if(!code.equals(dto.getCode())){
            throw new BizException("验证码错误");
        }
        User user = this.getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getEmail, dto.getEmail()));
        if(user == null){
            throw new BizException("该账户不存在");
        }
        //缓存token
        String token = createToken(user.getId());
        //删除验证码
        redis.delete(key);
        LoginVo loginVo = BeanUtil.copyProperties(user, LoginVo.class);
        loginVo.setToken(token);
        loginVo.setTags(getUserTags(user.getId()));
        return loginVo;
    }

    @Override
    public LoginVo login(UserLoginDto dto) {
        User user = this.getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getUsername, dto.getUsername())
               );
        if(user == null){
            throw new BizException("账户不存在");
        }
        if(!passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())){
            throw new BizException("密码错误");
        }
        String token = createToken(user.getId());
        LoginVo loginVo = BeanUtil.copyProperties(user, LoginVo.class);
        loginVo.setToken(token);
        loginVo.setTags(getUserTags(user.getId()));
        return loginVo;
    }

    @Override
    public void logout(String token) {
        redis.delete(RedisConstant.USER_LOGIN_TOKEN + token);
    }

    @Override
    @Transactional
    public void tagBinding(List<Long> tagIds) {
        //先删除biaoq
        userTagMapper.delete(new LambdaQueryWrapper<UserTag>()
                .eq(UserTag::getUserId, UserContext.getCurrentUserId()));
        List<UserTag> collect = tagIds.stream()
                .map(tagid -> {
                    UserTag userTag = new UserTag();
                    userTag.setUserId(UserContext.getCurrentUserId());
                    userTag.setTagId(tagid);
                    return userTag;
                }).collect(Collectors.toList());
        userTagMapper.insert(collect);
    }

    /**
     * 根据标签分页查询用户
     * @param dto
     * @return
     */
    @Override
    public Page<GetUserByTagsVo> getUserByTag(GetUserByTagsDto dto) {
        long current = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        long size = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : Math.min(dto.getPageSize(), 100);
        return userTagMapper.getUserByTag(new Page<>(current, size), dto.getTagIds());
    }


    /**
     * 用户详情（脱敏，含绑定的标签树）
     */
    @Override
    public UserDetailVo getUserDetail(UserDetailDto dto) {
        User user = this.getById(dto.getId());
        if (user == null) {
            throw new BizException("用户不存在");
        }
        UserDetailVo vo = BeanUtil.copyProperties(user, UserDetailVo.class);
        vo.setTags(getUserTags(user.getId()));
        return vo;
    }


    /**
     * 完善个人信息：仅更新传入的字段，返回更新后的最新资料（不含 token）
     */
    @Override
    public LoginVo updateUser(UpdateUserDto dto) {
        Long uid = UserContext.getCurrentUserId();
        User update = new User();
        update.setId(uid);
        if (dto.getUsername() != null) update.setUsername(dto.getUsername());
        if (dto.getAvatarUrl() != null) update.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getGender() != null) update.setGender(dto.getGender());
        if (dto.getPhone() != null) update.setPhone(dto.getPhone());
        // 全部字段都没传时不执行 UPDATE，否则 MP 会生成非法的 "UPDATE user SET WHERE id=?"
        if (update.getUsername() != null || update.getAvatarUrl() != null
                || update.getGender() != null || update.getPhone() != null) {
            this.updateById(update);
        }

        LoginVo vo = BeanUtil.copyProperties(this.getById(uid), LoginVo.class);
        vo.setTags(getUserTags(uid));
        return vo;
    }


    /**
     * 获取用户绑定的tags
     * @return
     */

    public List<TagVo> getUserTags(Long userId) {
        List<Tag> userTags = tagMapper.getUserTags(userId);
        if (userTags.isEmpty()) {
            return new ArrayList<>();
        }
        // 绑定的都是二级标签，其父分类不在绑定关系里，补查父标签用于组装树
        List<Long> parentIds = userTags.stream()
                .map(Tag::getParentId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .filter(pid -> userTags.stream().noneMatch(t -> t.getId().equals(pid)))
                .collect(java.util.stream.Collectors.toList());
        if (!parentIds.isEmpty()) {
            userTags.addAll(tagMapper.selectBatchIds(parentIds));
        }
        HashMap<Long, TagVo> map = new HashMap<>();
        ArrayList<TagVo> list = new ArrayList<>();
        for(Tag tag : userTags){
            map.put(tag.getId(),BeanUtil.copyProperties(tag, TagVo.class));
        }
        for(TagVo tagVo : map.values()){
            if(tagVo.getParentId() == null){
                list.add(tagVo);
            }
            else{
                TagVo parent = map.get(tagVo.getParentId());
                if(parent != null){
                    if(parent.getChildren() == null){
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(tagVo);
                }
            }
        }
        return list;
    }


    public String createToken(Long id){
        String token = RandomUtil.randomString(32);
        String tokenKey = (RedisConstant.USER_LOGIN_TOKEN +token );
        redis.opsForValue().set(tokenKey, String.valueOf(id), RedisConstant.USER_LOGIN_TOKEN_EXPIRE, TimeUnit.MINUTES);
        log.info("create token {}", token);
        return token;
    }
}
