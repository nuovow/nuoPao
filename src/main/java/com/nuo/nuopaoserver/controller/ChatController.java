package com.nuo.nuopaoserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.service.ChatMessageService;
import com.nuo.nuopaoserver.vo.ChatMessageVo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天 REST 层：历史消息 / 已读。实时收发走 /ws，这里只负责拉取和标记
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Data
    public static class HistoryDto {
        /** 聊天对象 */
        @NotNull(message = "聊天对象不能为空")
        private Long userId;
        private Long page = 1L;
        private Long pageSize = 20L;
    }

    @Data
    public static class ReadDto {
        @NotNull(message = "聊天对象不能为空")
        private Long userId;
    }

    @GetMapping("/history")
    public Result history(@Validated HistoryDto dto) {
        Long me = UserContext.getCurrentUserId();
        Page<ChatMessageVo> page = chatMessageService.history(me, dto.getUserId(), dto.getPage(), dto.getPageSize());
        return Result.success(page);
    }

    @PostMapping("/read")
    public Result markRead(@Validated @RequestBody ReadDto dto) {
        chatMessageService.markRead(UserContext.getCurrentUserId(), dto.getUserId());
        return Result.success();
    }
}
