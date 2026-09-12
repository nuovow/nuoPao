package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.nuo.nuopaoserver.dto.FollowPageDto;
import com.nuo.nuopaoserver.entity.UserFollow;
import com.nuo.nuopaoserver.enums.FollowStatus;
import com.nuo.nuopaoserver.vo.FollowPageVo;

public interface UserFollowService extends IService<UserFollow> {
    void follow(Long id);

    FollowStatus getStatus(Long id);

    void unfollow(Long id);

    Page<FollowPageVo> followList(FollowPageDto dto);

    // TODO 接口方法按需追加：
    //  - void follow(Long followUserId)        关注（不能关注自己、幂等）
    //  - void unfollow(Long followUserId)      取关
    //  - FollowStatusVo status(Long targetId)  { followed, followMe, mutual }
    //  - Page<...> followList(...) / fansList(...)  分页列表（联用户表）
}
