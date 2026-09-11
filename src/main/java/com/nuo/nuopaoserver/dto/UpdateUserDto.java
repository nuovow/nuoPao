package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 完善个人信息（全部字段可选，仅更新传入的字段）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    @Size(min = 2, max = 20, message = "用户名长度在 2 到 20 位之间")
    private String username;

    private String avatarUrl;

    /**
     * 性别 0-女 1-男 2-未知
     */
    @Min(value = 0, message = "性别取值不正确")
    @Max(value = 2, message = "性别取值不正确")
    private Integer gender;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
