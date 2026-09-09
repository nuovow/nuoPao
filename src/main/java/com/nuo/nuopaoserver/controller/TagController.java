package com.nuo.nuopaoserver.controller;

import com.nuo.nuopaoserver.common.Result;
import com.nuo.nuopaoserver.dto.TagCrateDto;
import com.nuo.nuopaoserver.service.TagService;
import com.nuo.nuopaoserver.vo.TagVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    /**
     * 增加标签
     * @param tagCrateDto
     * @return
     */
    @PostMapping
    public Result addTag(@Valid @RequestBody TagCrateDto tagCrateDto){
        tagService.saveTag(tagCrateDto);
        return Result.success();
    }

    /**
     * 获取全部标签（树结构）
     */
    @GetMapping
    public Result getTags(){
        List<TagVo> list = tagService.getTags();
        return Result.success(list);
    }
}
