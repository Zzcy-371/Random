package com.random.app.controller;

import com.random.app.dto.FeedbackCreateDTO;
import com.random.app.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/decisions/{decisionId}/feedback")
    public Map<String, Object> submitFeedback(Authentication auth, @PathVariable Long decisionId,
                                              @Valid @RequestBody FeedbackCreateDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "反馈成功");
        result.put("data", feedbackService.submitFeedback(userId, decisionId, dto));
        return result;
    }
}
