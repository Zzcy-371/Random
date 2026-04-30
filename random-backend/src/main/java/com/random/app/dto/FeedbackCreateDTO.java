package com.random.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackCreateDTO {
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小1")
    @Max(value = 3, message = "评分最大3")
    private Integer rating;

    private String comment;
}
