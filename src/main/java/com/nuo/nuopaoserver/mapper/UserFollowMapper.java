package com.nuo.nuopaoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.dto.FollowPageDto;
import com.nuo.nuopaoserver.entity.UserFollow;
import com.nuo.nuopaoserver.vo.FollowPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    Page<FollowPageVo> followList(Page<FollowPageVo> page, @Param("dto") FollowPageDto dto);

    // TODO 自定义 SQL 按需追加：
    //  - 互关列表（自连接）：f1.userId=me join f2 on f1.userId=f2.followUserId and f2.userId=f1.followUserId
    //  - 关注/粉丝列表分页联用户表返回昵称头像
}
