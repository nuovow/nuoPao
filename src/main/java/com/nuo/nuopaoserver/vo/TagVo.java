package com.nuo.nuopaoserver.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVo {
    private Long id;
    private String tagName;
    private List<TagVo> children;

}
