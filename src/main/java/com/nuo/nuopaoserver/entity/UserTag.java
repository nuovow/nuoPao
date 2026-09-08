package com.nuo.nuopaoserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户标签关系
 *
 * @TableName user_tag
 *
 * 注意：该表使用 (userId, tagId) 联合主键，没有独立 id。
 * MyBatis-Plus 的 deleteById / updateById / selectById 不适用，请自定义 SQL 操作。
 */
@Data
@TableName("user_tag")
public class UserTag implements Serializable {

    /**
     * 用户id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 标签id
     */
    @TableField("tagId")
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
