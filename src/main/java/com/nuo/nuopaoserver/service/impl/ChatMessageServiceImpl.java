package com.nuo.nuopaoserver.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.entity.ChatMessage;
import com.nuo.nuopaoserver.mapper.ChatMessageMapper;
import com.nuo.nuopaoserver.service.ChatMessageService;
import com.nuo.nuopaoserver.vo.ChatMessageVo;
import com.nuo.nuopaoserver.vo.ChatSessionVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    /**
     * 保存一条私聊消息（isRead 默认未读，createTime 由 DB 填充）
     */
    @Override
    public ChatMessage saveMessage(Long fromUserId, Long toUserId, String content, String clientId) {
        ChatMessage message = new ChatMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setClientId(clientId);
        this.save(message);
        return message;
    }

    /**
     * 我和某人的聊天记录：(我发他 or 他发我)，按 id 倒序分页
     */
    @Override
    public Page<ChatMessageVo> history(Long me, Long peer, long page, long size) {
        Page<ChatMessage> result = this.lambdaQuery()
                .and(c -> c.eq(ChatMessage::getFromUserId, me).eq(ChatMessage::getToUserId, peer))
                .or(c -> c.eq(ChatMessage::getFromUserId, peer).eq(ChatMessage::getToUserId, me))
                .orderByDesc(ChatMessage::getId)
                .page(new Page<>(page, size));
        // VO 转换（雪花 id 在响应层已是字符串，前端可直接当 key）
        Page<ChatMessageVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(m -> new ChatMessageVo(m.getId(), m.getFromUserId(), m.getContent(), m.getCreateTime()))
                .toList());
        return voPage;
    }

    /**
     * 对方发给我的未读消息全部标记已读
     */
    @Override
    public void markRead(Long me, Long peer) {
        this.lambdaUpdate()
                .eq(ChatMessage::getFromUserId, peer)
                .eq(ChatMessage::getToUserId, me)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1)
                .update();
    }

    /**
     * 会话列表
     */
    @Override
    public List<ChatSessionVo> sessions(Long me) {
        return this.baseMapper.sessions(me);
    }
}
