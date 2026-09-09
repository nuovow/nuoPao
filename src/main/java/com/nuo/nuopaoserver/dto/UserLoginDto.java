package com.nuo.nuopaoserver.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {


    /**
     * 密码（兼容前端传 password / userPassword 两种字段名）
     */
    @JsonAlias("password")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6到20个字符之间")
    private String userPassword;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度在2到20个字符之间")
    private String username;



    /**
     * 星球编号（雪花ID，对外转Base62）
     */
    private Long planetCode;


}
