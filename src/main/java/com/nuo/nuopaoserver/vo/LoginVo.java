package com.nuo.nuopaoserver.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {


    /**
     * 用户昵称
     */
    private String username;



    /**
     * 用户头像
     */

    private String avatarUrl;

    /**
     * 性别 0-女 1-男 2-未知
     */
    private Integer gender;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */

    private String email;



    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;



    /**
     * 0-普通用户 1-管理员
     */

    private Integer userRole;

    /**
     * 星球编号（雪花ID，对外转Base62）
     */
    private String planetCode;

    private String token;

    private List<TagVo> tags;

}
