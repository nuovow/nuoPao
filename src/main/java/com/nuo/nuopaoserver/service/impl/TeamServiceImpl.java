package com.nuo.nuopaoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.dto.TeamCreateDto;
import com.nuo.nuopaoserver.dto.TeamJoinDto;
import com.nuo.nuopaoserver.entity.Team;
import com.nuo.nuopaoserver.entity.User;
import com.nuo.nuopaoserver.entity.UserTeam;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.TeamMapper;
import com.nuo.nuopaoserver.mapper.UserTeamMapper;
import com.nuo.nuopaoserver.service.TeamService;
import com.nuo.nuopaoserver.service.UserService;
import com.nuo.nuopaoserver.vo.TeamMemberVo;
import com.nuo.nuopaoserver.vo.TeamVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    /** 状态：0-公开 1-私有 2-加密 */
    private static final int STATUS_PUBLIC = 0;
    private static final int STATUS_ENCRYPTED = 2;

    private final UserTeamMapper userTeamMapper;
    private final UserService userService;

    /**
     * 创建队伍：创建人即队长，自动入队；带密码则为加密队伍
     */
    @Override
    @Transactional
    public Team createTeam(TeamCreateDto dto) {
        Long me = UserContext.getCurrentUserId();
        Team team = BeanUtil.copyProperties(dto, Team.class);
        team.setUserId(me);
        team.setStatus(StringUtils.hasText(dto.getPassword()) ? STATUS_ENCRYPTED : STATUS_PUBLIC);
        this.save(team);

        UserTeam member = new UserTeam();
        member.setUserId(me);
        member.setTeamId(team.getId());
        member.setJoinTime(LocalDateTime.now());
        userTeamMapper.insert(member);
        return team;
    }

    /**
     * 加入队伍：校验存在/过期/已满/加密密码，已在队伍中则拒绝
     */
    @Override
    public void joinTeam(TeamJoinDto dto) {
        Long me = UserContext.getCurrentUserId();
        Team team = this.getById(dto.getTeamId());
        if (team == null) {
            throw new BizException("队伍不存在");
        }
        if (team.getExpireTime() != null && team.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BizException("队伍已过期");
        }
        if (inTeam(team.getId(), me)) {
            throw new BizException("你已在队伍中");
        }
        if (memberCount(team.getId()) >= team.getMaxNum()) {
            throw new BizException("队伍已满");
        }
        if (team.getStatus() == STATUS_ENCRYPTED
                && !StringUtils.hasText(dto.getPassword())) {
            throw new BizException("该队伍需要加入密码");
        }
        if (team.getStatus() == STATUS_ENCRYPTED
                && !team.getPassword().equals(dto.getPassword())) {
            throw new BizException("加入密码错误");
        }
        UserTeam member = new UserTeam();
        member.setUserId(me);
        member.setTeamId(team.getId());
        member.setJoinTime(LocalDateTime.now());
        userTeamMapper.insert(member);
    }

    /**
     * 退出队伍：队长退出等于解散（逻辑删队伍 + 清成员），普通成员只删自己的关系
     */
    @Override
    @Transactional
    public void quitTeam(Long teamId) {
        Long me = UserContext.getCurrentUserId();
        Team team = getValidTeam(teamId);
        if (!inTeam(teamId, me)) {
            throw new BizException("你不在该队伍中");
        }
        if (team.getUserId().equals(me)) {
            dissolveInternal(team);
            return;
        }
        removeMembership(teamId, me);
    }

    /**
     * 解散队伍（仅队长）
     */
    @Override
    @Transactional
    public void dissolveTeam(Long teamId) {
        Long me = UserContext.getCurrentUserId();
        Team team = getValidTeam(teamId);
        if (!team.getUserId().equals(me)) {
            throw new BizException("只有队长能解散队伍");
        }
        dissolveInternal(team);
    }

    /**
     * 队伍广场：未过期队伍，按创建时间倒序分页，附带人数/队长昵称
     */
    @Override
    public Page<TeamVo> listTeams(long page, long size) {
        Page<Team> result = this.lambdaQuery()
                .and(c -> c.isNull(Team::getExpireTime).or().gt(Team::getExpireTime, LocalDateTime.now()))
                .orderByDesc(Team::getCreateTime)
                .page(new Page<>(page, size));
        Page<TeamVo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(toVos(result.getRecords()));
        return voPage;
    }

    /**
     * 我加入的队伍（未过期的）
     */
    @Override
    public List<TeamVo> myTeams() {
        Long me = UserContext.getCurrentUserId();
        List<Long> teamIds = userTeamMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                                .eq(UserTeam::getUserId, me))
                .stream().map(UserTeam::getTeamId).toList();
        if (teamIds.isEmpty()) {
            return List.of();
        }
        List<Team> teams = this.listByIds(teamIds).stream()
                .filter(t -> t.getExpireTime() == null || t.getExpireTime().isAfter(LocalDateTime.now()))
                .toList();
        return toVos(teams);
    }

    /**
     * 队伍成员列表（含队长标记）
     */
    @Override
    public List<TeamMemberVo> members(Long teamId) {
        Team team = getValidTeam(teamId);
        List<Long> userIds = userTeamMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                                .eq(UserTeam::getTeamId, teamId))
                .stream().map(UserTeam::getUserId).toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        Map<Long, User> users = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return userIds.stream()
                .map(uid -> {
                    User u = users.get(uid);
                    return new TeamMemberVo(
                            uid,
                            u == null ? "已注销" : u.getUsername(),
                            u == null ? null : u.getAvatarUrl(),
                            u == null ? null : u.getPlanetCode(),
                            team.getUserId().equals(uid));
                })
                .toList();
    }

    // ---------- 内部工具 ----------

    private Team getValidTeam(Long teamId) {
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BizException("队伍不存在");
        }
        return team;
    }

    private boolean inTeam(Long teamId, Long userId) {
        return userTeamMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                        .eq(UserTeam::getTeamId, teamId)
                        .eq(UserTeam::getUserId, userId)) > 0;
    }

    private long memberCount(Long teamId) {
        return userTeamMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                        .eq(UserTeam::getTeamId, teamId));
    }

    private void removeMembership(Long teamId, Long userId) {
        userTeamMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                        .eq(UserTeam::getTeamId, teamId)
                        .eq(UserTeam::getUserId, userId));
    }

    /** 解散：逻辑删队伍 + 清空成员关系 */
    private void dissolveInternal(Team team) {
        this.removeById(team.getId());
        userTeamMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                        .eq(UserTeam::getTeamId, team.getId()));
        log.info("队伍已解散：{}（{}）", team.getName(), team.getId());
    }

    /** 组装 TeamVo：批量补人数和队长昵称，避免循环查库 */
    private List<TeamVo> toVos(List<Team> teams) {
        if (teams.isEmpty()) {
            return List.of();
        }
        List<Long> teamIds = teams.stream().map(Team::getId).toList();
        Map<Long, Long> counts = userTeamMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserTeam>()
                                .in(UserTeam::getTeamId, teamIds))
                .stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId, Collectors.counting()));
        Map<Long, User> owners = userService.listByIds(
                        teams.stream().map(Team::getUserId).toList()).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return teams.stream()
                .map(t -> new TeamVo(
                        t.getId(),
                        t.getName(),
                        t.getDescription(),
                        t.getMaxNum(),
                        counts.getOrDefault(t.getId(), 0L),
                        t.getStatus() == STATUS_ENCRYPTED,
                        owners.containsKey(t.getUserId()) ? owners.get(t.getUserId()).getUsername() : "已注销",
                        t.getCreateTime()))
                .toList();
    }
}
