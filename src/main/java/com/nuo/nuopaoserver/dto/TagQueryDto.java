package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagQueryDto {

    @Size(max = 20, message = "标签名称不能超过20个字符")
    private String tagName;
    private Integer page = 0;
    private Integer pageSize = 10;
}
