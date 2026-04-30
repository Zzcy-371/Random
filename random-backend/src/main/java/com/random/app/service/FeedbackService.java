package com.random.app.service;

import com.random.app.dto.FeedbackCreateDTO;
import com.random.app.vo.FeedbackVO;

public interface FeedbackService {
    FeedbackVO submitFeedback(Long userId, Long decisionId, FeedbackCreateDTO dto);
}
