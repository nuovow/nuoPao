package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuo.nuopaoserver.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT * FROM tag WHERE id IN (SELECT tagId FROM user_tag WHERE userId = #{userId})")
    List<Tag> getUserTags(@Param("userId") Long userId);
}
