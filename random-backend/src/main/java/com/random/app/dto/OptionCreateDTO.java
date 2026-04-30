package com.random.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OptionCreateDTO {
    @NotBlank(message = "选项名称不能为空")
    @Size(max = 200, message = "名称最长200个字符")
    private String name;

    private String description;

    @Size(max = 500, message = "标签最长500个字符")
    private String tags;

    private String imageUrl;
}
