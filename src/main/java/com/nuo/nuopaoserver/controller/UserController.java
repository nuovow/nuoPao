package com.nuo.nuopaoserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.*;
import com.nuo.nuopaoserver.service.UserService;
import com.nuo.nuopaoserver.vo.GetUserByTagsVo;
import com.nuo.nuopaoserver.vo.LoginVo;
import com.nuo.nuopaoserver.vo.TagVo;
import com.nuo.nuopaoserver.vo.UserDetailVo;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("token");
        userService.logout(token);
        return Result.success();
    }

    @PostMapping("/tags/binding")
    public Result tagBinding(@RequestBody List<Long> tagIds){
        userService.tagBinding(tagIds);
        return Result.success();
    }

    @GetMapping("/getByTag")
    public Result getUserByTag(@Valid  GetUserByTagsDto dto){
        Page<GetUserByTagsVo> page = userService.getUserByTag(dto);
        return Result.success(page);
    }

    @GetMapping("/detail")
    public Result getUserDetail(@Valid UserDetailDto dto){
        UserDetailVo vo = userService.getUserDetail(dto);
        return Result.success(vo);
    }

    @PostMapping("/update")
    public Result updateUser(@Valid @RequestBody UpdateUserDto dto){
        LoginVo vo = userService.updateUser(dto);
        return Result.success(vo);
    }



}
