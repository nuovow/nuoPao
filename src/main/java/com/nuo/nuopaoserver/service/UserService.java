package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.spring.service.IService;

import com.nuo.nuopaoserver.dto.UserRegisterDto;
import com.nuo.nuopaoserver.dto.UserRegisterGetCode;
import com.nuo.nuopaoserver.entity.User;
import jakarta.validation.Valid;

public interface UserService extends IService<User> {
    void registerGetCode(@Valid UserRegisterGetCode dto);

    void register(@Valid UserRegisterDto dto);
}
