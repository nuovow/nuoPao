package com.nuo.nuopaoserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 队伍
 *
 * @TableName team
 */
@Data
@TableName("team")
public class Team implements Serializable {

    /**
     * id（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 队伍名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 最大人数
     */
    @TableField("maxNum")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @TableField("expireTime")
    private LocalDateTime expireTime;

    /**
     * 创建人id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 0-公开 1-私有 2-加密
     */
    @TableField("status")
    private Integer status;

    /**
     * 密码（加密存储，仅status=2时有值）
     */
    @TableField("password")
    private String password;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除 0-未删 1-已删
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
