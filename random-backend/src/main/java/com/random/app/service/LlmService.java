package com.random.app.service;

import java.util.List;
import java.util.Map;

public interface LlmService {

    /**
     * 智能推荐：基于用户历史和时间段，给出个性化推荐理由
     */
    String getSmartRecommendation(String category, List<String> options, String timeOfDay, Map<String, Double> preferences);

    /**
     * 分析用户偏好：从历史决策中分析用户的深层偏好
     */
    String analyzePreferences(String category, List<String> decisionHistory, Map<String, Double> preferences);

    /**
     * 智能建议：根据已有选项，推荐新的可能选项
     */
    List<String> suggestNewOptions(String category, List<String> existingOptions, Map<String, Double> preferences);

    /**
     * 决策解释：解释为什么推荐某个选项
     */
    String explainDecision(String chosenOption, List<String> alternatives, String timeOfDay, Map<String, Double> preferences);

    /**
     * 直接调用LLM，返回原始响应（用于调试）
     */
    String chatDirect(String userMessage);
}
