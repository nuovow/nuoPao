package com.nuo.nuopaoserver.controller;

import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.UserRegisterDto;
import com.nuo.nuopaoserver.dto.UserRegisterGetCode;
import com.nuo.nuopaoserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
