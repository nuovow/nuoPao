package com.nuo.nuopaoserver.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowPageVo {
    private Long id;
    private String username;
    private String avatarUrl;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
    private String planetCode;
    /**
     * 关注时间（来自关系表，非用户创建时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime followTime;
}
