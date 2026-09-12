package com.nuo.nuopaoserver.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowPageDto {

    /**
     * 要查看的用户id，为空默认查当前登录用户
     */
    private Long userId;
    private Long page = 1L;
    private Long pageSize = 10L;
}
