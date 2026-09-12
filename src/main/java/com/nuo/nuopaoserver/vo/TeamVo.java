package com.nuo.nuopaoserver.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamVo {
    private Long id;
    private String name;
    private String description;
    private Integer maxNum;
    /** 当前人数 */
    private Long memberCount;
    /** 是否加密队伍 */
    private Boolean encrypted;
    private String ownerName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
}
