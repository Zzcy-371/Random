package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiRecommendationVO {
    private String category;
    private String recommendation;
    private String explanation;
    private List<String> suggestedOptions;
    private String analysis;
}
