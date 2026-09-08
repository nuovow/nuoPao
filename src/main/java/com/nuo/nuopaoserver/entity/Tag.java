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
 * 标签
 *
 * @TableName tag
 */
@Data
@TableName("tag")
public class Tag implements Serializable {

    /**
     * id（雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签名
     */
    @TableField("tagName")
    private String tagName;

    /**
     * 上传标签的用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 父标签id（null表示一级标签）
     */
    @TableField("parentId")
    private Long parentId;

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
