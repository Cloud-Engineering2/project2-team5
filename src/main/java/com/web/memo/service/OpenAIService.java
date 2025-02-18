package com.web.memo.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;

import java.util.List;

@Service
public class OpenAIService {

    private final OpenAiService openAiService;

    public OpenAIService(@Value("${spring.ai.openai.api-key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String summarizeText(String text) {
        String prompt = "Summarize the following text in exactly 3 sentences and under 150 characters:\n" + text;

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(150)
                .temperature(0.7)
                .build();

        var result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent().trim();
    }

    public String createTitle(String text) {
        String prompt = "Create a title for the given content:\n" + text;

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(150)
                .temperature(0.7)
                .build();

        var result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent().trim();
    }
}
