package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;

import com.nuo.nuopaoserver.dto.*;
import com.nuo.nuopaoserver.entity.User;
import com.nuo.nuopaoserver.vo.GetUserByTagsVo;
import com.nuo.nuopaoserver.vo.LoginVo;
import com.nuo.nuopaoserver.vo.UserDetailVo;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService extends IService<User> {
    void registerGetCode(@Valid UserRegisterGetCode dto);

    void register(@Valid UserRegisterDto dto);

    void loginGetCode(@Valid UserLoginGetCode dto);

    LoginVo emailLogin(@Valid EmailLoginDto dto);

    LoginVo login(@Valid UserLoginDto dto);

    void logout(String token);


    void tagBinding(List<Long> tagIds);

    Page<GetUserByTagsVo> getUserByTag(@Valid GetUserByTagsDto dto);

    UserDetailVo getUserDetail(@Valid UserDetailDto dto);

    LoginVo updateUser(@Valid UpdateUserDto dto);
}
