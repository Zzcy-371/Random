package com.random.app.service.impl;

import com.random.app.entity.Category;
import com.random.app.entity.Option;
import com.random.app.entity.UserPreference;
import com.random.app.repository.CategoryRepository;
import com.random.app.repository.DecisionRepository;
import com.random.app.repository.OptionRepository;
import com.random.app.repository.UserPreferenceRepository;
import com.random.app.service.RecommendationService;
import com.random.app.vo.DailyRecommendationVO;
import com.random.app.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final CategoryRepository categoryRepository;
    private final OptionRepository optionRepository;
    private final DecisionRepository decisionRepository;
    private final UserPreferenceRepository preferenceRepository;

    @Override
    public DailyRecommendationVO getDailyRecommendations(Long userId) {
        int hour = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).getHour();
        TimePeriod period = getTimePeriod(hour);

        Map<String, List<OptionVO>> recommendations = new LinkedHashMap<>();
        for (String catName : period.categories) {
            Category cat = categoryRepository.findByName(catName).orElse(null);
            if (cat == null) continue;
            List<Option> options = optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, cat.getId());
            if (options.isEmpty()) continue;

            // Exclude options chosen today
            LocalDateTime todayStart = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).toLocalDate().atStartOfDay();
            List<Long> chosenToday = decisionRepository.findRecentChosenOptionIds(userId, cat.getId(), todayStart);
            List<Option> available = options.stream()
                    .filter(o -> !chosenToday.contains(o.getId()))
                    .toList();
            if (available.isEmpty()) available = options;

            // Score by preference weight
            List<UserPreference> prefs = preferenceRepository.findByUserIdAndCategoryId(userId, cat.getId());
            Map<String, BigDecimal> prefMap = prefs.stream()
                    .collect(Collectors.toMap(UserPreference::getTag, UserPreference::getWeight));

            List<Option> top3 = available.stream()
                    .sorted((a, b) -> Double.compare(scoreOption(b, prefMap), scoreOption(a, prefMap)))
                    .limit(3)
                    .toList();

            recommendations.put(cat.getDisplayName(), top3.stream().map(this::toVO).toList());
        }

        return DailyRecommendationVO.builder()
                .timePeriod(period.label)
                .message(period.message)
                .recommendationsByCategory(recommendations)
                .build();
    }

    @Override
    public DailyRecommendationVO getWeeklyRecommendations(Long userId) {
        // Simplified: return top options across all categories
        Map<String, List<OptionVO>> recommendations = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findAll();

        for (Category cat : categories) {
            List<Option> options = optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, cat.getId());
            if (options.isEmpty()) continue;
            List<UserPreference> prefs = preferenceRepository.findByUserIdAndCategoryId(userId, cat.getId());
            Map<String, BigDecimal> prefMap = prefs.stream()
                    .collect(Collectors.toMap(UserPreference::getTag, UserPreference::getWeight));

            List<Option> top5 = options.stream()
                    .sorted((a, b) -> Double.compare(scoreOption(b, prefMap), scoreOption(a, prefMap)))
                    .limit(5)
                    .toList();
            recommendations.put(cat.getDisplayName(), top5.stream().map(this::toVO).toList());
        }

        return DailyRecommendationVO.builder()
                .timePeriod("weekly")
                .message("本周精选推荐")
                .recommendationsByCategory(recommendations)
                .build();
    }

    private double scoreOption(Option option, Map<String, BigDecimal> prefMap) {
        double score = 0;
        for (String tag : option.getTagList()) {
            BigDecimal w = prefMap.getOrDefault(tag, new BigDecimal("0.5"));
            score += w.doubleValue();
        }
        return score;
    }

    private OptionVO toVO(Option option) {
        return OptionVO.builder()
                .id(option.getId())
                .categoryId(option.getCategory().getId())
                .categoryName(option.getCategory().getDisplayName())
                .name(option.getName())
                .description(option.getDescription())
                .tags(option.getTagList())
                .imageUrl(option.getImageUrl())
                .createdAt(option.getCreatedAt())
                .build();
    }

    private TimePeriod getTimePeriod(int hour) {
        if (hour >= 6 && hour < 11) return new TimePeriod("breakfast", "早餐时光",
                "新的一天，从美食开始！", List.of("eating", "drinking"));
        if (hour >= 11 && hour < 14) return new TimePeriod("lunch", "午餐时间",
                "忙碌了一上午，犒劳一下自己", List.of("eating"));
        if (hour >= 14 && hour < 18) return new TimePeriod("afternoon", "下午茶",
                "来杯饮品提提神", List.of("drinking"));
        if (hour >= 18 && hour < 21) return new TimePeriod("dinner", "晚餐时分",
                "享受晚餐的美好时光", List.of("eating"));
        if (hour >= 21 && hour < 24) return new TimePeriod("evening", "夜间娱乐",
                "放松一下，享受夜晚", List.of("playing"));
        return new TimePeriod("late_night", "深夜",
                "夜深了，注意休息哦", List.of("staying"));
    }

    private record TimePeriod(String label, String displayName, String message, List<String> categories) {}
}
