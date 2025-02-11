package com.web.memo.controller;

import com.web.memo.dto.SummaryDto;
import com.web.memo.service.SummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/summary")
public class SummaryController {

    private SummaryService summaryService;

    @Autowired
    public void setSummaryService(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> summaryFromGetDTO(@RequestBody Map<String, String> requestBody) {
        Map<String, String> response = new HashMap<>();

        try {
            String summary = requestBody.get("content");
            String summaryText = summaryService.setSummary(summary);
            response.put("summary", summaryText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "요약 처리 중 오류가 발생했습니다.");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}