package com.nuo.nuopaoserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 私聊消息
 *
 * @TableName chat_message
 *
 * 注意：雪花 id 天然递增，兼做消息排序依据；clientId 用于弱网重发去重
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {

    /**
     * id（雪花算法生成，兼做消息排序依据）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发送方
     */
    @TableField("fromUserId")
    private Long fromUserId;

    /**
     * 接收方
     */
    @TableField("toUserId")
    private Long toUserId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 前端生成的去重id（弱网重发防重复）
     */
    @TableField("clientId")
    private String clientId;

    /**
     * 是否已读 0-未读 1-已读
     */
    @TableField("isRead")
    private Integer isRead;

    /**
     * 发送时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
