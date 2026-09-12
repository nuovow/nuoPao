package com.nuo.nuopaoserver.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberVo {
    private Long id;
    private String username;
    private String avatarUrl;
    private String planetCode;
    /** 是否队长 */
    private Boolean isOwner;
}
