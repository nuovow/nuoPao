package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.nuo.nuopaoserver.entity.ChatMessage;
import com.nuo.nuopaoserver.vo.ChatMessageVo;

public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 保存一条私聊消息
     */
    ChatMessage saveMessage(Long fromUserId, Long toUserId, String content, String clientId);

    /**
     * 我和某人的聊天记录，按消息 id 倒序分页（前端渲染时自行升序）
     */
    Page<ChatMessageVo> history(Long me, Long peer, long page, long size);

    /**
     * 把对方发给我的未读消息标记为已读
     */
    void markRead(Long me, Long peer);
}
