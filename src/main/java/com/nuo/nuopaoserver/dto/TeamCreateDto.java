package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamCreateDto {

    @NotBlank(message = "队伍名称不能为空")
    @Size(min = 1, max = 20, message = "队伍名称长度在 1 到 20 位之间")
    private String name;

    @Size(max = 200, message = "队伍描述最多 200 字")
    private String description;

    @Min(value = 1, message = "队伍人数至少 1 人")
    @Max(value = 20, message = "队伍人数最多 20 人")
    private Integer maxNum;

    /** 传入则创建加密队伍（status=2），不传为公开队伍 */
    @Size(max = 16, message = "加入密码最多 16 位")
    private String password;
}
