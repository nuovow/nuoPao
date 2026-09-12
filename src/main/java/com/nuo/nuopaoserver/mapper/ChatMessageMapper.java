package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuo.nuopaoserver.entity.ChatMessage;
import com.nuo.nuopaoserver.vo.ChatSessionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 会话列表：每个聊过天的人一行（最后一条消息 + 未读数），按最后消息时间倒序
     */
    List<ChatSessionVo> sessions(@Param("me") Long me);
}

