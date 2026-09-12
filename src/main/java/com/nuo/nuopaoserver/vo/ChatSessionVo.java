package com.nuo.nuopaoserver.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话列表项：每个聊过天的人一行（最后一条消息 + 未读数）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionVo {
    /** 对方用户id */
    private String peerId;
    private String username;
    private String avatarUrl;
    private String planetCode;
    /** 最后一条消息内容 */
    private String lastContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastTime;
    /** 未读条数（对方发给我且未读） */
    private Long unread;
}
