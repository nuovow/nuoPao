package com.nuo.nuopaoserver.controller;

import cn.hutool.core.util.RandomUtil;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.constant.RedisConstant;
import com.nuo.nuopaoserver.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WS 握手票据：前端先用 HTTP token 换一次性 ticket，再拿 ticket 去握手，
 * 避免登录 token 裸露在 ws:// URL 上（URL 会进访问日志/代理日志）
 */
@RestController
@RequiredArgsConstructor
public class WsTicketController {

    private final StringRedisTemplate redis;

    @PostMapping("/ws/ticket")
    public Result ticket() {
        Long me = UserContext.getCurrentUserId();
        String ticket = RandomUtil.randomString(32);
        redis.opsForValue().set(RedisConstant.WS_TICKET + ticket, String.valueOf(me),
                RedisConstant.WS_TICKET_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return Result.success(Map.of("ticket", ticket));
    }
}
