package com.nuo.nuopaoserver.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.nuo.nuopaoserver.entity.Tag;
import com.nuo.nuopaoserver.mapper.TagMapper;
import com.nuo.nuopaoserver.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
