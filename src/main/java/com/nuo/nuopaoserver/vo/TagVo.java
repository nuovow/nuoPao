package com.nuo.nuopaoserver.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.time.LocalDateTime;

public class TagVo {

    private String tagName;

    private String  createUser;

    private String avatarUrl;

    private String parentTag;


}
