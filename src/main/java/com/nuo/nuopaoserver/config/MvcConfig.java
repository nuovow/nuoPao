package com.nuo.nuopaoserver.config;

import com.nuo.nuopaoserver.interceptor.RefreshTokenInterceptor;
import com.nuo.nuopaoserver.interceptor.GlobalInterCeptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final StringRedisTemplate redis;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefreshTokenInterceptor(redis)).order(1);
        registry.addInterceptor(new GlobalInterCeptor(redis)).order(2)
                .excludePathPatterns("/user/login/*"
                , "/user/register/*");
    }
}
