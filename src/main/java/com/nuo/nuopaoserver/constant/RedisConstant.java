package com.nuo.nuopaoserver.constant;

import lombok.Data;

@Data
public class RedisConstant {
    public static final String USER_REGISTER_CODE = "user_register_code:";
    public static final Long USER_REGISTER_CODE_EXPIRE = 5L;
    public static final String USER_LOGIN = "user_login:";
    public static final Long USER_LOGIN_EXPIRE = 5L;
    public static final String USER_LOGIN_TOKEN = "user_login_token:";
    public static final Long USER_LOGIN_TOKEN_EXPIRE = 60L;
    // RedisConstant 里加两个前缀，和现有风格统一
    public static final String USER_FOLLOW = "user:follow:";   // 后面拼 userId → 我关注了谁
    public static final String USER_FANS   = "user:fans:";     // 后面拼 userId → 谁关注了我
}
