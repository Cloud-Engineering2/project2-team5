package com.web.memo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.theokanning.openai.service.OpenAiService;

@Configuration
public class OpenAIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey);
    }
}
