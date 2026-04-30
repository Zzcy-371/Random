package com.random.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "llm")
@Getter
@Setter
public class LlmConfig {
    private boolean enabled = true;
    private String baseUrl = "https://api.siliconflow.cn/v1";
    private String apiKey = "";
    private String model = "Qwen/Qwen2.5-7B-Instruct";
    private int timeout = 30000;
}
