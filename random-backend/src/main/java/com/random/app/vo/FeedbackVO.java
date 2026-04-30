package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackVO {
    private Long id;
    private Long decisionId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
