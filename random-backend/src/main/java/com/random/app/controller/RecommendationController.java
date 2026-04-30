package com.random.app.controller;

import com.random.app.service.PreferenceService;
import com.random.app.service.RecommendationService;
import com.random.app.vo.PreferenceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final PreferenceService preferenceService;

    @GetMapping("/categories/{catId}/preferences")
    public Map<String, Object> getPreferences(Authentication auth, @PathVariable Long catId) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", preferenceService.getPreferences(userId, catId));
        return result;
    }

    @PutMapping("/categories/{catId}/preferences")
    public Map<String, Object> updatePreferences(Authentication auth, @PathVariable Long catId,
                                                 @RequestBody List<PreferenceVO> preferences) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "更新成功");
        result.put("data", preferenceService.updatePreferences(userId, catId, preferences));
        return result;
    }

    @GetMapping("/recommendations/daily")
    public Map<String, Object> getDailyRecommendations(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", recommendationService.getDailyRecommendations(userId));
        return result;
    }

    @GetMapping("/recommendations/weekly")
    public Map<String, Object> getWeeklyRecommendations(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", recommendationService.getWeeklyRecommendations(userId));
        return result;
    }
}
