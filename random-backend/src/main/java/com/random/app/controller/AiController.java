package com.random.app.controller;

import com.random.app.entity.Option;
import com.random.app.entity.UserPreference;
import com.random.app.repository.CategoryRepository;
import com.random.app.repository.DecisionRepository;
import com.random.app.repository.OptionRepository;
import com.random.app.repository.UserPreferenceRepository;
import com.random.app.service.LlmService;
import com.random.app.vo.AiRecommendationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final LlmService llmService;
    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final DecisionRepository decisionRepository;

    @GetMapping("/recommend/{catId}")
    public Map<String, Object> getAiRecommendation(Authentication auth, @PathVariable Long catId) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();

        List<Option> options = optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, catId);
        if (options.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "请先添加选项");
            return result;
        }

        String categoryName = categoryRepository.findById(catId).map(c -> c.getDisplayName()).orElse("未知");
        List<String> optionNames = options.stream().map(Option::getName).toList();
        Map<String, Double> prefs = getPreferencesMap(userId, catId);
        String timeOfDay = getTimeOfDay();

        String recommendation = llmService.getSmartRecommendation(categoryName, optionNames, timeOfDay, prefs);
        String explanation = null;
        if (recommendation != null) {
            explanation = llmService.explainDecision(
                    optionNames.get(0), optionNames.subList(1, Math.min(optionNames.size(), 4)), timeOfDay, prefs);
        }

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", AiRecommendationVO.builder()
                .category(categoryName)
                .recommendation(recommendation != null ? recommendation : "AI服务未配置，请在配置文件中设置LLM API Key")
                .explanation(explanation)
                .build());
        return result;
    }

    @GetMapping("/suggest/{catId}")
    public Map<String, Object> suggestOptions(Authentication auth, @PathVariable Long catId) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();

        List<Option> options = optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, catId);
        String categoryName = categoryRepository.findById(catId).map(c -> c.getDisplayName()).orElse("未知");
        List<String> existingNames = options.stream().map(Option::getName).toList();
        Map<String, Double> prefs = getPreferencesMap(userId, catId);

        List<String> suggestions = llmService.suggestNewOptions(categoryName, existingNames, prefs);

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", suggestions);
        return result;
    }

    @GetMapping("/analyze/{catId}")
    public Map<String, Object> analyzePreferences(Authentication auth, @PathVariable Long catId) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();

        String categoryName = categoryRepository.findById(catId).map(c -> c.getDisplayName()).orElse("未知");
        Map<String, Double> prefs = getPreferencesMap(userId, catId);

        // Get recent decision history
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Long> recentOptions = decisionRepository.findRecentChosenOptionIds(userId, catId, since);
        List<String> history = recentOptions.stream()
                .map(id -> optionRepository.findById(id).map(Option::getName).orElse("未知"))
                .toList();

        String analysis = llmService.analyzePreferences(categoryName, history, prefs);

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", analysis != null ? analysis : "AI服务未配置");
        return result;
    }

    private Map<String, Double> getPreferencesMap(Long userId, Long categoryId) {
        return preferenceRepository.findByUserIdAndCategoryId(userId, categoryId)
                .stream()
                .collect(Collectors.toMap(UserPreference::getTag, p -> p.getWeight().doubleValue()));
    }

    private String getTimeOfDay() {
        int hour = LocalDateTime.now().getHour();
        if (hour >= 6 && hour < 11) return "早晨";
        if (hour >= 11 && hour < 14) return "中午";
        if (hour >= 14 && hour < 18) return "下午";
        if (hour >= 18 && hour < 22) return "晚上";
        return "深夜";
    }
}
