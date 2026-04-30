package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OptionVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private List<String> tags;
    private String imageUrl;
    private LocalDateTime createdAt;
}
