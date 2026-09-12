package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuo.nuopaoserver.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    // TODO 历史消息分页时追加：两个人之间的记录按 id 倒序分页（联查或纯条件查询均可）
}
