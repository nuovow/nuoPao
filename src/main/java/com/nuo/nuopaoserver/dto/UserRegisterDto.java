package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotNull(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度在2到20个字符之间")
    private String username;
    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6到20个字符之间")
    private String password;
    @NotNull(message = "验证码不能为空")
    private String code;
}
