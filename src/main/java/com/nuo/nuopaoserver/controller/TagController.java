package com.nuo.nuopaoserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.TagCrateDto;
import com.nuo.nuopaoserver.entity.Tag;
import com.nuo.nuopaoserver.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    @PostMapping
    public Result addTag(@Valid @RequestBody TagCrateDto tagCrateDto){
        tagService.save(BeanUtil.copyProperties(tagCrateDto, Tag.class));
        return Result.success();
    }
}
