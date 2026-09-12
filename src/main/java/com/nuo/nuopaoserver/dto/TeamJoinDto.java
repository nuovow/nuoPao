package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamJoinDto {

    @NotNull(message = "队伍id不能为空")
    private Long teamId;

    /** 加密队伍必填 */
    @Size(max = 16, message = "加入密码最多 16 位")
    private String password;
}
