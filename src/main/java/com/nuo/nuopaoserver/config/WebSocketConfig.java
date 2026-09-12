package com.nuo.nuopaoserver.config;

import com.nuo.nuopaoserver.ws.ChatWebSocketHandler;
import com.nuo.nuopaoserver.ws.WSHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final WSHandshakeInterceptor wsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws")
                .addInterceptors(wsHandshakeInterceptor)
                // 前端 5173 直连 8080 属跨域，不放行握手会被浏览器拒绝
                .setAllowedOriginPatterns("*");
    }
}
