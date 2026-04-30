package com.random.app.service;

import com.random.app.vo.PreferenceVO;

import java.util.List;

public interface PreferenceService {
    List<PreferenceVO> getPreferences(Long userId, Long categoryId);
    List<PreferenceVO> updatePreferences(Long userId, Long categoryId, List<PreferenceVO> preferences);
    void adjustWeights(Long userId, Long decisionId, int rating);
}
