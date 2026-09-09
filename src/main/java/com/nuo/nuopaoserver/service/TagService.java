package com.nuo.nuopaoserver.service;

import com.baomidou.mybatisplus.spring.service.IService;

import com.nuo.nuopaoserver.dto.TagCrateDto;
import com.nuo.nuopaoserver.entity.Tag;
import com.nuo.nuopaoserver.vo.TagVo;

import java.util.List;

public interface TagService extends IService<Tag> {

    /**
     * 创建标签（含同名重复校验）
     */
    void saveTag(TagCrateDto dto);

    /**
     * 查询全部标签并组装为树
     */
    List<TagVo> getTags();
}
