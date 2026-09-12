package com.nuo.nuopaoserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注关系
 *
 * @TableName user_follow
 *
 * 注意：uni_follower(userId, followUserId) 唯一索引防重复关注，重复插入会抛 DuplicateKey。
 * userId + followUserId 的两步写请保持同步，互关判断用查询计算（自连接或 Redis Set），不在表里存冗余标记。
 */
@Data
@TableName("user_follow")
public class UserFollow implements Serializable {

    /**
     * id（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关注者（发起方）
     */
    @TableField("userId")
    private Long userId;

    /**
     * 被关注者
     */
    @TableField("followUserId")
    private Long followUserId;

    /**
     * 关注时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
