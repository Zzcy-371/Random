package com.random.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.random.app.config.LlmConfig;
import com.random.app.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final LlmConfig llmConfig;
    private final ObjectMapper objectMapper;

    @Override
    public String getSmartRecommendation(String category, List<String> options, String timeOfDay, Map<String, Double> preferences) {
        String prompt = String.format(
                "你是一个生活助手。用户正在「%s」类别下做选择。\n" +
                "当前时间段：%s\n" +
                "可选选项：%s\n" +
                "用户偏好权重：%s\n\n" +
                "请用2-3句话推荐最合适的选项，并简要说明理由。语气轻松友好。",
                category, timeOfDay, String.join("、", options), formatPreferences(preferences)
        );
        return chat(prompt);
    }

    @Override
    public String analyzePreferences(String category, List<String> decisionHistory, Map<String, Double> preferences) {
        String prompt = String.format(
                "你是一个数据分析助手。请分析用户在「%s」类别的决策习惯。\n" +
                "历史决策记录：%s\n" +
                "偏好权重：%s\n\n" +
                "请总结用户的偏好模式，指出有趣的发现，用2-3句话表达。",
                category, String.join("、", decisionHistory), formatPreferences(preferences)
        );
        return chat(prompt);
    }

    @Override
    public List<String> suggestNewOptions(String category, List<String> existingOptions, Map<String, Double> preferences) {
        String prompt = String.format(
                "请为「%s」推荐3个新选项。已有：%s。只输出名称，每行一个。",
                category, String.join("、", existingOptions)
        );
        String response = chat(prompt);
        log.info("AI建议新选项 - prompt长度: {}, 响应: {}", prompt.length(), response);
        if (response == null || response.isBlank()) return List.of();
        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .map(s -> s.replaceAll("^[#\\-*\\d]+[.、)）:：]?\\s*", ""))
                .filter(s -> !s.isEmpty() && s.length() < 20)
                .limit(3)
                .toList();
    }

    @Override
    public String explainDecision(String chosenOption, List<String> alternatives, String timeOfDay, Map<String, Double> preferences) {
        String prompt = String.format(
                "你是一个有趣的决策助手。随机决策选中了「%s」，备选有：%s。\n" +
                "当前时间段：%s，用户偏好：%s\n\n" +
                "请用1-2句幽默有趣的话解释为什么这个选择不错。",
                chosenOption, String.join("、", alternatives), timeOfDay, formatPreferences(preferences)
        );
        return chat(prompt);
    }

    private synchronized String chat(String userMessage) {
        if (!llmConfig.isEnabled() || llmConfig.getApiKey().isBlank() || llmConfig.getApiKey().equals("your-api-key-here")) {
            log.info("LLM未启用或API Key未配置: enabled={}, keyBlank={}", llmConfig.isEnabled(), llmConfig.getApiKey().isBlank());
            return null;
        }
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(llmConfig.getTimeout()))
                        .build();

                Map<String, Object> body = Map.of(
                        "model", llmConfig.getModel(),
                        "messages", List.of(
                                Map.of("role", "system", "content", "你是Random随机决策应用的AI助手，回答简洁友好，使用中文。"),
                                Map.of("role", "user", "content", userMessage)
                        ),
                        "temperature", 0.7,
                        "max_tokens", 500
                );

                String jsonBody = objectMapper.writeValueAsString(body);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(llmConfig.getBaseUrl() + "/chat/completions"))
                        .timeout(Duration.ofMillis(llmConfig.getTimeout()))
                        .header("Authorization", "Bearer " + llmConfig.getApiKey())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                log.info("正在调用LLM API (尝试{}/{}): {}", attempt, maxRetries, llmConfig.getBaseUrl());
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("LLM API响应状态: {}, body长度: {}", response.statusCode(), response.body().length());

                if (response.statusCode() == 200) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> respMap = objectMapper.readValue(response.body(), Map.class);
                    if (respMap.containsKey("choices")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                        if (!choices.isEmpty()) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                            return (String) message.get("content");
                        }
                    }
                } else {
                    log.error("LLM API返回错误: status={}, body={}", response.statusCode(), response.body());
                }
                return null;
            } catch (Exception e) {
                log.error("LLM调用失败 (尝试{}/{}): {} - {}", attempt, maxRetries, e.getClass().getSimpleName(), e.getMessage());
                if (attempt < maxRetries) {
                    try { Thread.sleep(3000L * attempt); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                }
            }
        }
        return null;
    }

    private String formatPreferences(Map<String, Double> preferences) {
        if (preferences == null || preferences.isEmpty()) return "暂无偏好数据";
        StringBuilder sb = new StringBuilder();
        preferences.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> sb.append(e.getKey()).append("(").append(String.format("%.0f%%", e.getValue() * 100)).append(") "));
        return sb.toString().trim();
    }
}
