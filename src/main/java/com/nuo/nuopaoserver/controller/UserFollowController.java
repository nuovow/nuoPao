package com.nuo.nuopaoserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.FollowPageDto;
import com.nuo.nuopaoserver.enums.FollowStatus;
import com.nuo.nuopaoserver.service.UserFollowService;
import com.nuo.nuopaoserver.vo.FollowPageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@Slf4j
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService userFollowService;

    @PostMapping("/follow/{id}")
    public Result follow(@PathVariable Long id) {
        userFollowService.follow(id);
        return Result.success();
    }

    @GetMapping("/status/{id}")
    public Result status(@PathVariable Long id) {
        FollowStatus status = userFollowService.getStatus(id);
        return Result.success(status);
    }

    @PostMapping("/unfollow/{id}")
    public Result unfollow(@PathVariable Long id) {
        userFollowService.unfollow(id);
        return Result.success();
    }

    @GetMapping("/followList")
    public Result followList(FollowPageDto dto) {
        Page<FollowPageVo> followPage = userFollowService.followList(dto);
        return Result.success(followPage);
    }

    // TODO 接口自己实现，建议：
    //  POST /follow            关注（body 传 followUserId）
    //  POST /unfollow          取关
    //  GET  /status?id=        关系状态 { followed, followMe, mutual }
    //  GET  /followList?page=&pageSize=   我的关注（分页）
    //  GET  /fansList?page=&pageSize=     我的粉丝（分页）
}
