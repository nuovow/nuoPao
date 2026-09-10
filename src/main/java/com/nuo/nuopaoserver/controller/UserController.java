package com.nuo.nuopaoserver.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.*;
import com.nuo.nuopaoserver.service.UserService;
import com.nuo.nuopaoserver.vo.LoginVo;
import com.nuo.nuopaoserver.vo.TagVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register/getCode")
    public Result registerGetCode(@Valid @RequestBody UserRegisterGetCode dto) {
      userService.registerGetCode(dto);

        return Result.success();
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDto dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login/getCode")
    public Result loginGetCode(@Valid @RequestBody UserLoginGetCode dto){
        userService.loginGetCode(dto);
        return Result.success();
    }

        @PostMapping("/login/email")
        public Result login(@Valid @RequestBody EmailLoginDto dto){
           LoginVo loginVo = userService.emailLogin(dto);
           return Result.success(loginVo);
        }
        @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDto dto){
        LoginVo loginVo = userService.login(dto);
        return Result.success(loginVo);
    }

    @PostMapping("/logout")
    public Result logout(HttpServerRequest request){
        String token = request.getHeader("token");
        userService.logout(token);
        return Result.success();
    }

    @PostMapping("/tags/binding")
    public Result tagBinding(@RequestBody List<Long> tagIds){
        userService.tagBinding(tagIds);
        return Result.success();
    }


}
