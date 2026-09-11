package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.entity.UserTag;
import com.nuo.nuopaoserver.vo.GetUserByTagsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {


    /**
     * 根据标签分页查询匹配的用户，page 中携带 total/records
     * @param page 分页参数（current、size 由 MybatisPlusInterceptor 拼接 limit）
     * @param tagIds
     * @return
     */
    Page<GetUserByTagsVo> getUserByTag(Page<GetUserByTagsVo> page, @Param("tagIds") List<Long> tagIds);
}
