package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuo.nuopaoserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
