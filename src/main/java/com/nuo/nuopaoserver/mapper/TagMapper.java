package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuo.nuopaoserver.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    @Select("select tag.* from user_tag join tag on tag.id = user_tag.tagId " +
            "where user_tag.userId = #{userId} and tag.isDelete = 0")
    List<Tag> getUserTags(@Param("userId") Long userId);
}
