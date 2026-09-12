package com.nuo.nuopaoserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.TeamCreateDto;
import com.nuo.nuopaoserver.dto.TeamJoinDto;
import com.nuo.nuopaoserver.entity.Team;
import com.nuo.nuopaoserver.service.TeamService;
import com.nuo.nuopaoserver.vo.TeamMemberVo;
import com.nuo.nuopaoserver.vo.TeamVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/create")
    public Result create(@Valid @RequestBody TeamCreateDto dto) {
        Team team = teamService.createTeam(dto);
        return Result.success(team.getId());
    }

    @PostMapping("/join")
    public Result join(@Valid @RequestBody TeamJoinDto dto) {
        teamService.joinTeam(dto);
        return Result.success();
    }

    @PostMapping("/quit/{teamId}")
    public Result quit(@PathVariable Long teamId) {
        teamService.quitTeam(teamId);
        return Result.success();
    }

    @PostMapping("/dissolve/{teamId}")
    public Result dissolve(@PathVariable Long teamId) {
        teamService.dissolveTeam(teamId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") long page,
                       @RequestParam(defaultValue = "10") long pageSize) {
        Page<TeamVo> result = teamService.listTeams(page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result my() {
        List<TeamVo> list = teamService.myTeams();
        return Result.success(list);
    }

    @GetMapping("/members/{teamId}")
    public Result members(@PathVariable Long teamId) {
        List<TeamMemberVo> members = teamService.members(teamId);
        return Result.success(members);
    }
}
