package com.nuo.nuopaoserver.ws;

import com.nuo.nuopaoserver.constant.RedisConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手认证：优先 header token（能设置 header 的客户端），URL 上则验证一次性票据
 * （前端先 POST /ws/ticket 换 ticket，再拿 ws://...?ticket=xxx 握手，用后即焚，主 token 不进 URL 日志）
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
        String userId = resolveUserId(request);
        if (userId == null) {
            return false;  // 无凭据或凭据无效 → 拒绝握手
        }
        // 身份挂到本次连接上，Handler 里 session.getAttributes().get("userId") 随取
        attributes.put("userId", Long.parseLong(userId));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 不需要
    }

    /**
     * 双来源获取身份：header token 优先（通用客户端），URL ticket 兜底（浏览器）
     */
    private String resolveUserId(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String headerToken = servletRequest.getServletRequest().getHeader("token");
            if (StringUtils.hasText(headerToken)) {
                return redis.opsForValue().get(RedisConstant.USER_LOGIN_TOKEN + headerToken);
            }
        }
        String ticket = urlParam(request, "ticket");
        if (!StringUtils.hasText(ticket)) {
            return null;
        }
        // 一次性：验完即删，重放无效
        String key = RedisConstant.WS_TICKET + ticket;
        String userId = redis.opsForValue().get(key);
        if (userId != null) {
            redis.delete(key);
        }
        return userId;
    }

    private String urlParam(ServerHttpRequest request, String name) {
        String query = request.getURI().getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            if (param.startsWith(name + "=")) {
                return param.substring(name.length() + 1);
            }
        }
        return null;
    }
}
