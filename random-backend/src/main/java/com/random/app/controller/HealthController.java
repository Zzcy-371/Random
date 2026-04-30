package com.random.app.controller;

import com.random.app.config.LlmConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final LlmConfig llmConfig;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "timestamp", System.currentTimeMillis());
    }

    @GetMapping("/api/health/llm")
    public Map<String, Object> llmStatus() {
        String key = llmConfig.getApiKey();
        String masked = (key == null || key.isBlank()) ? "(empty)" : key.substring(0, Math.min(8, key.length())) + "***";
        return Map.of(
                "enabled", llmConfig.isEnabled(),
                "baseUrl", llmConfig.getBaseUrl(),
                "apiKeyMasked", masked,
                "model", llmConfig.getModel()
        );
    }
}
