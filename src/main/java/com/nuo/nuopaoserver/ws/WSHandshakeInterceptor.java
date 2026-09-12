package com.nuo.nuopaoserver.ws;

import com.nuo.nuopaoserver.constant.RedisConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手认证：token 双来源获取——header 优先（能设置 header 的客户端，如单测/服务间调用），
 * URL 参数兜底（浏览器原生 WebSocket API 不支持自定义请求头，只能 ?token=xxx）
 */
@Component
public class WSHandshakeInterceptor implements HandshakeInterceptor {

    private final StringRedisTemplate redis;

    public WSHandshakeInterceptor(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = resolveToken(request);
        if (token == null) {
            return false;  // header 和 URL 都没有 → 未登录，拒绝握手
        }
        String value = redis.opsForValue().get(RedisConstant.USER_LOGIN_TOKEN + token);
        if (value == null) {
            return false;  // token 过期/不存在
        }
        // 身份挂到本次连接上，Handler 里 session.getAttributes().get("userId") 随取
        attributes.put("userId", Long.parseLong(value));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 不需要
    }

    /**
     * 双来源取 token：header 优先，取不到再解析 URL ?token=xxx
     */
    private String resolveToken(ServerHttpRequest request) {
        // 浏览器的 WS 握手不会带自定义 header，但能带的客户端优先走 header（与 HTTP 接口同源）
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String headerToken = servletRequest.getServletRequest().getHeader("token");
            if (headerToken != null && !headerToken.isBlank()) {
                return headerToken;
            }
        }
        String query = request.getURI().getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }
        return null;
    }
}
