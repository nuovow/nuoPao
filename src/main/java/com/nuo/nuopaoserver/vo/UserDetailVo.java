package com.nuo.nuopaoserver.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情（对外脱敏：不含密码、手机号、邮箱）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailVo {
    private Long id;
    private String username;
    private String avatarUrl;
    private Integer gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
    private String planetCode;
    private List<TagVo> tags;
}
