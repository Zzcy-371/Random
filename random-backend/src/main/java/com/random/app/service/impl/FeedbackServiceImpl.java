package com.random.app.service.impl;

import com.random.app.dto.FeedbackCreateDTO;
import com.random.app.entity.Decision;
import com.random.app.entity.Feedback;
import com.random.app.exception.BusinessException;
import com.random.app.repository.DecisionRepository;
import com.random.app.repository.FeedbackRepository;
import com.random.app.repository.UserRepository;
import com.random.app.service.FeedbackService;
import com.random.app.service.PreferenceService;
import com.random.app.vo.FeedbackVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final DecisionRepository decisionRepository;
    private final UserRepository userRepository;
    private final PreferenceService preferenceService;

    @Override
    public FeedbackVO submitFeedback(Long userId, Long decisionId, FeedbackCreateDTO dto) {
        if (feedbackRepository.findByDecisionId(decisionId).isPresent()) {
            throw new BusinessException("该决策已有反馈");
        }
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new BusinessException("决策记录不存在"));
        if (!decision.getUser().getId().equals(userId)) {
            throw new BusinessException(403, "无权对该决策反馈");
        }

        Feedback feedback = new Feedback();
        feedback.setDecision(decision);
        feedback.setUser(userRepository.getReferenceById(userId));
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedbackRepository.save(feedback);

        // Adjust preference weights based on feedback
        preferenceService.adjustWeights(userId, decisionId, dto.getRating());

        return FeedbackVO.builder()
                .id(feedback.getId())
                .decisionId(decisionId)
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
