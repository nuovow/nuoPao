package com.nuo.nuopaoserver.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.time.LocalDateTime;

public class UserLoginDto {


    /**
     * 密码（加密存储）
     */
    private String userPassword;


    /**
     * 邮箱
     */
    @TableField("email")
    private String email;



    /**
     * 星球编号（雪花ID，对外转Base62）
     */
    private Long planetCode;


}
