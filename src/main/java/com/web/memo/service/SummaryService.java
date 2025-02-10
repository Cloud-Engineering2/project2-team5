package com.web.memo.service;

import com.web.memo.dto.SummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SummaryService {

    private final OpenAIService openAIService;

    @Autowired
    public SummaryService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    public SummaryDTO setSummary(String summary) {

        String summaryText = openAIService.summarizeText(summary);

        return new SummaryDTO(summary, summaryText);
    }
}
