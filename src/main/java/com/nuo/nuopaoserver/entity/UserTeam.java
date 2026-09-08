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
 * 用户队伍关系
 *
 * @TableName user_team
 */
@Data
@TableName("user_team")
public class UserTeam implements Serializable {

    /**
     * id（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 队伍id
     */
    @TableField("teamId")
    private Long teamId;

    /**
     * 加入时间
     */
    @TableField("joinTime")
    private LocalDateTime joinTime;

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
