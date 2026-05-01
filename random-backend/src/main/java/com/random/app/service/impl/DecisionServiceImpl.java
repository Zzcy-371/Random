package com.random.app.service.impl;

import com.random.app.dto.DecideRequestDTO;
import com.random.app.entity.Category;
import com.random.app.entity.Decision;
import com.random.app.entity.Option;
import com.random.app.entity.UserPreference;
import com.random.app.exception.BusinessException;
import com.random.app.repository.*;
import com.random.app.service.DecisionService;
import com.random.app.vo.DecisionVO;
import com.random.app.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionServiceImpl implements DecisionService {

    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final DecisionRepository decisionRepository;
    private final UserPreferenceRepository preferenceRepository;

    @Override
    public DecisionVO decide(Long userId, Long categoryId, DecideRequestDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        // Step 1: Get all active options
        List<Option> allOptions = optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, categoryId);
        if (allOptions.isEmpty()) {
            throw new BusinessException("该分类下没有可用选项，请先添加");
        }

        // Step 2: Filter by preference tags
        List<Option> candidates = allOptions;
        if (dto.getPreferenceFilters() != null && !dto.getPreferenceFilters().isEmpty()) {
            List<String> filters = dto.getPreferenceFilters();
            List<Option> filtered = allOptions.stream()
                    .filter(opt -> opt.getTagList().stream()
                            .anyMatch(filters::contains))
                    .toList();
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }

        // Step 3: Exclude recent choices
        List<Long> recentOptionIds = List.of();
        if (Boolean.TRUE.equals(dto.getExcludeRecent())) {
            int days = dto.getExcludeRecentDays() != null ? dto.getExcludeRecentDays() : 3;
            LocalDateTime since = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusDays(days);
            recentOptionIds = decisionRepository.findRecentChosenOptionIds(userId, categoryId, since);
            if (!recentOptionIds.isEmpty()) {
                List<Long> excludeIds = recentOptionIds;
                List<Option> nonRecent = candidates.stream()
                        .filter(opt -> !excludeIds.contains(opt.getId()))
                        .toList();
                // Step 4: Fall back if too few remain
                if (nonRecent.size() >= 2) {
                    candidates = nonRecent;
                }
            }
        }

        // Step 5: Calculate weights
        List<UserPreference> preferences = preferenceRepository.findByUserIdAndCategoryId(userId, categoryId);
        Map<String, BigDecimal> prefMap = preferences.stream()
                .collect(Collectors.toMap(UserPreference::getTag, UserPreference::getWeight));

        List<WeightedOption> weightedOptions = new ArrayList<>();
        for (Option opt : candidates) {
            double weight = 1.0;
            for (String tag : opt.getTagList()) {
                BigDecimal tagWeight = prefMap.getOrDefault(tag, new BigDecimal("0.5"));
                weight *= (0.5 + tagWeight.doubleValue());
            }
            // Reduce weight for recently chosen (but not excluded)
            if (recentOptionIds.contains(opt.getId())) {
                weight *= 0.3;
            }
            // Add small jitter
            weight += Math.random() * 0.1;
            weightedOptions.add(new WeightedOption(opt, weight));
        }

        // Step 6: Weighted random selection
        Option chosen = weightedRandomSelect(weightedOptions);

        // Step 7: Choose alternates (top 2 by weight, excluding chosen)
        List<Option> alternates = weightedOptions.stream()
                .filter(wo -> !wo.option.getId().equals(chosen.getId()))
                .sorted((a, b) -> Double.compare(b.weight, a.weight))
                .limit(2)
                .map(wo -> wo.option)
                .toList();

        // Step 8: Record decision
        Decision decision = new Decision();
        decision.setUser(chosen.getUser());
        decision.setCategory(category);
        decision.setChosenOption(chosen);
        decision.setContextJson(buildContextJson(dto));
        decisionRepository.save(decision);

        // Step 9: Return result
        return DecisionVO.builder()
                .decisionId(decision.getId())
                .chosenOption(toVO(chosen))
                .alternates(alternates.stream().map(this::toVO).toList())
                .decidedAt(decision.getDecidedAt())
                .build();
    }

    @Override
    public Page<DecisionVO> getHistory(Long userId, Pageable pageable) {
        return decisionRepository.findByUserIdOrderByDecidedAtDesc(userId, pageable)
                .map(decision -> DecisionVO.builder()
                        .decisionId(decision.getId())
                        .chosenOption(toVO(decision.getChosenOption()))
                        .alternates(List.of())
                        .decidedAt(decision.getDecidedAt())
                        .build());
    }

    @Override
    public DecisionVO getDecision(Long userId, Long decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new BusinessException("决策记录不存在"));
        if (!decision.getUser().getId().equals(userId)) {
            throw new BusinessException(403, "无权访问该决策记录");
        }
        return DecisionVO.builder()
                .decisionId(decision.getId())
                .chosenOption(toVO(decision.getChosenOption()))
                .alternates(List.of())
                .decidedAt(decision.getDecidedAt())
                .build();
    }

    private Option weightedRandomSelect(List<WeightedOption> options) {
        double total = options.stream().mapToDouble(wo -> wo.weight).sum();
        double pick = Math.random() * total;
        double cumulative = 0;
        for (WeightedOption wo : options) {
            cumulative += wo.weight;
            if (pick <= cumulative) {
                return wo.option;
            }
        }
        return options.get(options.size() - 1).option;
    }

    private String buildContextJson(DecideRequestDTO dto) {
        int hour = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).getHour();
        String timeOfDay;
        if (hour >= 6 && hour < 11) timeOfDay = "morning";
        else if (hour >= 11 && hour < 14) timeOfDay = "noon";
        else if (hour >= 14 && hour < 18) timeOfDay = "afternoon";
        else if (hour >= 18 && hour < 22) timeOfDay = "evening";
        else timeOfDay = "night";
        return String.format("{\"timeOfDay\":\"%s\",\"filters\":%s}", timeOfDay,
                dto.getPreferenceFilters() != null ? dto.getPreferenceFilters() : "[]");
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

    private static class WeightedOption {
        final Option option;
        final double weight;
        WeightedOption(Option option, double weight) {
            this.option = option;
            this.weight = weight;
        }
    }
}
