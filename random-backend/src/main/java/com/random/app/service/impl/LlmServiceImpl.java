package com.random.app.service.impl;

import com.random.app.config.LlmConfig;
import com.random.app.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final LlmConfig llmConfig;

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
                "你是一个生活助手。用户在「%s」类别下已有这些选项：%s。\n" +
                "用户偏好：%s\n\n" +
                "请推荐3个用户可能喜欢的新选项，每个选项一行，不要编号，不要解释。",
                category, String.join("、", existingOptions), formatPreferences(preferences)
        );
        String response = chat(prompt);
        if (response == null || response.isBlank()) return List.of();
        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
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

    private String chat(String userMessage) {
        if (!llmConfig.isEnabled() || llmConfig.getApiKey().isBlank() || llmConfig.getApiKey().equals("your-api-key-here")) {
            return null;
        }
        try {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(llmConfig.getTimeout());
            factory.setReadTimeout(llmConfig.getTimeout());

            RestClient restClient = RestClient.builder()
                    .baseUrl(llmConfig.getBaseUrl())
                    .requestFactory(factory)
                    .build();

            Map<String, Object> body = Map.of(
                    "model", llmConfig.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", "你是Random随机决策应用的AI助手，回答简洁友好，使用中文。"),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "temperature", 0.7,
                    "max_tokens", 300
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + llmConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("LLM调用失败: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
