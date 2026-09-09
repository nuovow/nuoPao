package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.spring.service.IService;

import com.nuo.nuopaoserver.dto.*;
import com.nuo.nuopaoserver.entity.User;
import com.nuo.nuopaoserver.vo.LoginVo;
import jakarta.validation.Valid;

public interface UserService extends IService<User> {
    void registerGetCode(@Valid UserRegisterGetCode dto);

    void register(@Valid UserRegisterDto dto);

    void loginGetCode(@Valid UserLoginGetCode dto);

    LoginVo emailLogin(@Valid EmailLoginDto dto);

    LoginVo login(@Valid UserLoginDto dto);

    void logout(String token);
}
