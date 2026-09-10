package com.nuo.nuopaoserver.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.nuo.nuopaoserver.vo.TagVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {


    /**
     * 密码（加密存储）
     */
    @Size(min = 8, max = 20, message = "密码长度在8到20个字符之间")
    private String userPassword;

    @Size(min = 2, max = 20, message = "用户名长度在2到20个字符之间")
    private String username;



    /**
     * 星球编号（雪花ID，对外转Base62）
     */
    private Long planetCode;


}
