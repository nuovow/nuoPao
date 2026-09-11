package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserByTagsDto {
    @NotNull(message = "标签ID列表不能为空")
    private List<Long> tagIds;
    private Long page = 1L;
    private Long pageSize = 10L;
}
