package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    @NotNull(message = "用户ID不能为空")
    private Long id;
}
