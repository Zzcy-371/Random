package com.random.app.controller;

import com.random.app.config.LlmConfig;
import com.random.app.service.LlmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final LlmConfig llmConfig;
    private final LlmService llmService;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "timestamp", System.currentTimeMillis());
    }

    @GetMapping("/api/health/llm")
    public Map<String, Object> llmStatus() {
        String key = llmConfig.getApiKey();
        String masked = (key == null || key.isBlank()) ? "(empty)" : key.substring(0, Math.min(8, key.length())) + "***";
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", llmConfig.isEnabled());
        result.put("baseUrl", llmConfig.getBaseUrl());
        result.put("apiKeyMasked", masked);
        result.put("model", llmConfig.getModel());

        // Actually test the LLM call
        try {
            String testResult = llmService.getSmartRecommendation("测试", List.of("选项A", "选项B"), "下午", Map.of());
            result.put("testCall", testResult != null ? "成功: " + testResult.substring(0, Math.min(50, testResult.length())) : "返回null");
        } catch (Exception e) {
            result.put("testCall", "异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return result;
    }
}
