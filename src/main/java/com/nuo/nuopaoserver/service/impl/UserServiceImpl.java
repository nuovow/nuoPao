package com.nuo.nuopaoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.constant.RedisConstant;
import com.nuo.nuopaoserver.dto.UserRegisterDto;
import com.nuo.nuopaoserver.dto.UserRegisterGetCode;
import com.nuo.nuopaoserver.entity.User;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.UserMapper;
import com.nuo.nuopaoserver.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final StringRedisTemplate redis;
    private final TemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.mail.username}")
    private String from;

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
        log.info("Sent registration code {} to {}", code, dto.getEmail());
        //读取填充模板
        Context context = new Context();
        context.setVariable("username",dto.getEmail().split("@")[0]);
        context.setVariable("expireMinute",RedisConstant.USER_REGISTER_CODE_EXPIRE);
        context.setVariable("code", code);
        String html = templateEngine.process("mail/register.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(dto.getEmail());
            mimeMessageHelper.setSubject("【NuoPao】注册邮箱验证码");
            mimeMessageHelper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
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
        user.setUserPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPlanetCode(planeCode);
        save(user);
        redis.delete(RedisConstant.USER_REGISTER_CODE + dto.getEmail());
    }
}
