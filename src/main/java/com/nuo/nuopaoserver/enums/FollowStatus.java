package com.nuo.nuopaoserver.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 当前用户与目标用户的关注关系
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)   // 关键：序列化成对象而不是字符串
public enum FollowStatus {

    NOT_FOLLOWING("NOT_FOLLOWING", "未关注"),
    FOLLOWING("FOLLOWING", "已关注"),
    MUTUAL("MUTUAL", "互相关注");

    private final String name;
    private final String desc;

    FollowStatus(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}