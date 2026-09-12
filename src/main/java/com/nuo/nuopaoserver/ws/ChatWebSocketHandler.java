package com.nuo.nuopaoserver.ws;

import com.nuo.nuopaoserver.dto.ChatWsMessage;
import com.nuo.nuopaoserver.entity.ChatMessage;
import com.nuo.nuopaoserver.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天 WebSocket 核心：
 * 连接注册表（userId → session）+ 消息分发（ping/chat）+ 存库后推送/回执
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /** 连接注册表：握手拦截器保证连进来的都带 userId；同账号多开会互相顶掉（v1 单连接） */
    private static final Map<Long, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        WebSocketSession old = SESSIONS.put(userId, session);
        // 同账号旧连接被顶掉时主动关闭，避免它还在收发
        closeQuietly(old);
        log.info("WS 连上：{}，当前在线 {}", userId, SESSIONS.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long fromUserId = (Long) session.getAttributes().get("userId");
        ChatWsMessage msg;
        try {
            msg = objectMapper.readValue(message.getPayload(), ChatWsMessage.class);
        } catch (Exception e) {
            log.warn("无法解析的 WS 消息：{}", message.getPayload());
            return;
        }
        switch (msg.getType() == null ? "" : msg.getType()) {
            case "ping" -> send(session, Map.of("type", "pong"));
            case "chat" -> handleChat(fromUserId, msg);
            default -> log.warn("未知消息类型：{}", msg.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        // 只清理自己这条连接：旧连接被顶掉后关闭也会走到这里，别误删新连接
        if (userId != null && SESSIONS.get(userId) == session) {
            SESSIONS.remove(userId);
        }
        log.info("WS 断开：{}，剩余在线 {}", userId, SESSIONS.size());
    }

    /**
     * 聊天主流程：存库 → 给发送方回执 ack → 对方在线则实时推送（离线只落库，靠历史接口拉取）
     */
    private void handleChat(Long fromUserId, ChatWsMessage msg) {
        if (msg.getTo() == null || !StringUtils.hasText(msg.getContent())) {
            return;
        }
        ChatMessage saved = chatMessageService.saveMessage(fromUserId, msg.getTo(), msg.getContent(), msg.getClientId());

        // 回执给发送方：前端凭 clientId 把"发送中"的气泡标记为已送达
        WebSocketSession sender = SESSIONS.get(fromUserId);
        if (sender != null && sender.isOpen()) {
            Map<String, Object> ack = new LinkedHashMap<>();
            ack.put("type", "ack");
            ack.put("clientId", msg.getClientId());
            ack.put("id", String.valueOf(saved.getId()));
            send(sender, ack);
        }
        // 实时推给接收方（对方离线时跳过，消息已在库里）
        WebSocketSession target = SESSIONS.get(msg.getTo());
        if (target != null && target.isOpen()) {
            Map<String, Object> push = new LinkedHashMap<>();
            push.put("type", "chat");
            push.put("from", String.valueOf(fromUserId));
            push.put("content", saved.getContent());
            push.put("id", String.valueOf(saved.getId()));
            push.put("clientId", msg.getClientId());
            send(target, push);
        }
    }

    /** session 的 sendMessage 非线程安全，统一加锁；推送失败不抛出（不影响存库结果） */
    private void send(WebSocketSession session, Map<String, Object> payload) {
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (Exception e) {
            log.warn("WS 推送失败：{}", e.getMessage());
        }
    }

    private void closeQuietly(WebSocketSession session) {
        if (session == null) {
            return;
        }
        try {
            session.close();
        } catch (Exception ignored) {
        }
    }
}
