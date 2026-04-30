package com.random.app.controller;

import com.random.app.config.LlmConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
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
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", llmConfig.isEnabled());
        result.put("baseUrl", llmConfig.getBaseUrl());
        result.put("apiKeyMasked", masked);
        result.put("model", llmConfig.getModel());

        // Test connectivity to SiliconFlow
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(llmConfig.getBaseUrl() + "/models"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + llmConfig.getApiKey())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result.put("connectivity", "status=" + response.statusCode() + ", bodyLen=" + response.body().length());
        } catch (Exception e) {
            result.put("connectivity", "FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return result;
    }
}
