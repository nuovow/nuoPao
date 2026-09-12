package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.nuo.nuopaoserver.dto.TeamCreateDto;
import com.nuo.nuopaoserver.dto.TeamJoinDto;
import com.nuo.nuopaoserver.entity.Team;
import com.nuo.nuopaoserver.vo.TeamMemberVo;
import com.nuo.nuopaoserver.vo.TeamVo;

import java.util.List;

public interface TeamService extends IService<Team> {

    Team createTeam(TeamCreateDto dto);

    void joinTeam(TeamJoinDto dto);

    /** 成员退出；队长退出则队伍解散 */
    void quitTeam(Long teamId);

    /** 解散队伍（仅队长） */
    void dissolveTeam(Long teamId);

    /** 公开队伍广场，未过期，按创建时间倒序 */
    Page<TeamVo> listTeams(long page, long size);

    /** 我加入的队伍 */
    List<TeamVo> myTeams();

    /** 队伍成员列表 */
    List<TeamMemberVo> members(Long teamId);
}
