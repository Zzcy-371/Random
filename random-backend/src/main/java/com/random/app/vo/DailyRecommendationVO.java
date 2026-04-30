package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DailyRecommendationVO {
    private String timePeriod;
    private String message;
    private Map<String, List<OptionVO>> recommendationsByCategory;
}
