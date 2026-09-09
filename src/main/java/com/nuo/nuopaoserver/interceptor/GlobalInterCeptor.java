package com.nuo.nuopaoserver.interceptor;

import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.constant.RedisConstant;
import com.nuo.nuopaoserver.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GlobalInterCeptor implements HandlerInterceptor {
    private final StringRedisTemplate redis;

    public GlobalInterCeptor(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void wire401Response(HttpServletResponse response, String message){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.removeCurrentUserId();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(token == null){
            wire401Response(response,"请登录");
            return false;
        }
        String redisKey = RedisConstant.USER_LOGIN_TOKEN + token;
        if(!redis.hasKey(redisKey)){
            wire401Response(response,"登录已过期"    );
            return false;
        }
        String s = redis.opsForValue().get(redisKey);
        UserContext.setCurrentUserId(Long.parseLong(s));
        return true;
    }
}
