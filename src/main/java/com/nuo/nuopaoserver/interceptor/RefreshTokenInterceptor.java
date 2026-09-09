package com.nuo.nuopaoserver.interceptor;

import com.nuo.nuopaoserver.constant.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redis;

    public RefreshTokenInterceptor(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(token == null){
            return true;
        }
        if(!redis.hasKey(token)){
            return true;
        }
        String redisKey = RedisConstant.USER_LOGIN_TOKEN + token;
        redis.expire(redisKey, RedisConstant.USER_LOGIN_TOKEN_EXPIRE, TimeUnit.MINUTES);
        return true;
    }
}
