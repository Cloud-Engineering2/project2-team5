package com.web.memo.controller;

import com.web.memo.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping
    public ResponseEntity<Map<String, String>> summarizeAndSave(@RequestParam String text) {
        try {
            // OpenAI API를 사용하여 요약 생성
            String summary = openAIService.summarizeText(text);

            // 사용자가 요청한 본문과 요약을 JSON 형태로 반환
            Map<String, String> response = new HashMap<>();
            response.put("originalText", text);
            response.put("summary", summary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
