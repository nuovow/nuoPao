package com.nuo.nuopaoserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 消息信封：前端每条消息都带 type，服务端按 type 分发处理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatWsMessage {

    /** chat-聊天消息 / ping-心跳 */
    private String type;

    /** 聊天对象（type=chat 时必填） */
    private Long to;

    /** 消息内容 */
    private String content;

    /** 前端生成的去重id（弱网重发防重复） */
    private String clientId;
}
