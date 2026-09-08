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
 * 用户
 *
 * @TableName user
 */
@Data
@TableName("`user`")
public class User implements Serializable {

    /**
     * id（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户昵称
     */
    @TableField("username")
    private String username;



    /**
     * 用户头像
     */
    @TableField("avatarUrl")
    private String avatarUrl;

    /**
     * 性别 0-女 1-男 2-未知
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 密码（加密存储）
     */
    @TableField("userPassword")
    private String userPassword;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 状态 0-正常
     */
    @TableField("userStatus")
    private Integer userStatus;

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

    /**
     * 0-普通用户 1-管理员
     */
    @TableField("userRole")
    private Integer userRole;

    /**
     * 星球编号（雪花ID，对外转Base62）
     */
    @TableField("planetCode")
    private String planetCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
