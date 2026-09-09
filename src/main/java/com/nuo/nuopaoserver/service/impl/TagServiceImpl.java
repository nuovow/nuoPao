package com.nuo.nuopaoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.context.UserContext;
import com.nuo.nuopaoserver.dto.TagCrateDto;
import com.nuo.nuopaoserver.entity.Tag;
import com.nuo.nuopaoserver.exception.BizException;
import com.nuo.nuopaoserver.mapper.TagMapper;
import com.nuo.nuopaoserver.service.TagService;
import com.nuo.nuopaoserver.vo.TagVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    /**
     * 标签最大层级，来自配置 tag.max-level
     */
    @Value("${tag.max-level}")
    private int maxLevel;

    /**
     * 创建标签
     * 1. 同名校验：全局范围内不允许重复的标签名
     * 2. 层级校验：level 由父标签的 level + 1 计算得出，超过 tag.max-level 则拒绝
     * 3. userId 从登录上下文取，不信任前端
     */
    @Override
    public void saveTag(TagCrateDto dto) {
        Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, dto.getTagName()));
        if (count != null && count > 0) {
            throw new BizException("标签[" + dto.getTagName() + "]已存在");
        }
        Tag tag = BeanUtil.copyProperties(dto, Tag.class);
        if (dto.getParentId() == null) {
            tag.setLevel(1);
        } else {
            Tag parent = this.getById(dto.getParentId());
            if (parent == null) {
                throw new BizException("父标签不存在");
            }
            if (parent.getLevel() + 1 > maxLevel) {
                throw new BizException("标签层级不能超过" + maxLevel + "层");
            }
            tag.setLevel(parent.getLevel() + 1);
        }
        tag.setUserId(UserContext.getCurrentUserId());
        this.save(tag);
    }

    /**
     * 获取标签
     * @return
     */
    @Override
    public List<TagVo> getTags() {
        // 按 id 排序，保证每次返回的树内顺序稳定
        List<Tag> list = this.list(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId));
        return createTagTree(list);
    }

    /**
     * 两遍法建树：第一遍建节点入 map，第二遍挂关系，与数据返回顺序无关
     */
    private List<TagVo> createTagTree(List<Tag> tags) {
        Map<Long, TagVo> map = new HashMap<>();
        for (Tag tag : tags) {
            map.put(tag.getId(), new TagVo(tag.getId(), tag.getTagName(), tag.getLevel(), null));
        }
        List<TagVo> roots = new ArrayList<>();
        for (Tag tag : tags) {
            TagVo vo = map.get(tag.getId());
            if (tag.getParentId() == null) {
                roots.add(vo);
            } else {
                TagVo parent = map.get(tag.getParentId());
                // 防御：父标签已被逻辑删除（孤儿节点）时丢弃，避免 NPE
                if (parent == null) {
                    continue;
                }
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(vo);
            }
        }
        return roots;
    }
}
