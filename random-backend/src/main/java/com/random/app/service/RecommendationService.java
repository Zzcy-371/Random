package com.random.app.service;

import com.random.app.vo.DailyRecommendationVO;

public interface RecommendationService {
    DailyRecommendationVO getDailyRecommendations(Long userId);
    DailyRecommendationVO getWeeklyRecommendations(Long userId);
}
