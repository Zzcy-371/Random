package com.random.app.service.impl;

import com.random.app.entity.Decision;
import com.random.app.entity.UserPreference;
import com.random.app.exception.BusinessException;
import com.random.app.repository.CategoryRepository;
import com.random.app.repository.DecisionRepository;
import com.random.app.repository.UserPreferenceRepository;
import com.random.app.repository.UserRepository;
import com.random.app.service.PreferenceService;
import com.random.app.vo.PreferenceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final DecisionRepository decisionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final BigDecimal MIN_WEIGHT = new BigDecimal("0.05");
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("0.95");
    private static final BigDecimal LEARNING_RATE = new BigDecimal("0.05");
    private static final BigDecimal DEFAULT_WEIGHT = new BigDecimal("0.5000");

    @Override
    public List<PreferenceVO> getPreferences(Long userId, Long categoryId) {
        return preferenceRepository.findByUserIdAndCategoryId(userId, categoryId)
                .stream()
                .map(p -> PreferenceVO.builder().tag(p.getTag()).weight(p.getWeight()).build())
                .toList();
    }

    @Override
    public List<PreferenceVO> updatePreferences(Long userId, Long categoryId, List<PreferenceVO> preferences) {
        return preferences.stream().map(pref -> {
            UserPreference entity = preferenceRepository
                    .findByUserIdAndCategoryIdAndTag(userId, categoryId, pref.getTag())
                    .orElseGet(() -> {
                        UserPreference np = new UserPreference();
                        np.setUser(userRepository.getReferenceById(userId));
                        np.setCategory(categoryRepository.getReferenceById(categoryId));
                        np.setTag(pref.getTag());
                        np.setWeight(DEFAULT_WEIGHT);
                        return np;
                    });
            entity.setWeight(clamp(pref.getWeight()));
            preferenceRepository.save(entity);
            return PreferenceVO.builder().tag(entity.getTag()).weight(entity.getWeight()).build();
        }).toList();
    }

    @Override
    public void adjustWeights(Long userId, Long decisionId, int rating) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new BusinessException("决策记录不存在"));
        Long categoryId = decision.getCategory().getId();

        for (String tag : decision.getChosenOption().getTagList()) {
            UserPreference pref = preferenceRepository
                    .findByUserIdAndCategoryIdAndTag(userId, categoryId, tag)
                    .orElseGet(() -> {
                        UserPreference np = new UserPreference();
                        np.setUser(decision.getUser());
                        np.setCategory(decision.getCategory());
                        np.setTag(tag);
                        np.setWeight(DEFAULT_WEIGHT);
                        preferenceRepository.save(np);
                        return np;
                    });

            BigDecimal current = pref.getWeight();
            BigDecimal newWeight;
            if (rating == 3) {
                // Satisfied: move toward 1.0
                newWeight = current.add(LEARNING_RATE.multiply(BigDecimal.ONE.subtract(current)));
            } else if (rating == 1) {
                // Not satisfied: move toward 0.0
                newWeight = current.subtract(LEARNING_RATE.multiply(current));
            } else {
                newWeight = current;
            }
            pref.setWeight(clamp(newWeight));
            preferenceRepository.save(pref);
        }
    }

    private BigDecimal clamp(BigDecimal weight) {
        if (weight.compareTo(MIN_WEIGHT) < 0) return MIN_WEIGHT;
        if (weight.compareTo(MAX_WEIGHT) > 0) return MAX_WEIGHT;
        return weight.setScale(4, RoundingMode.HALF_UP);
    }
}
