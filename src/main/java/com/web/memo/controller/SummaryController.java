package com.web.memo.controller;

import com.web.memo.dto.SummaryDTO;
import com.web.memo.service.SummaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/summary")
public class SummaryController {

    private SummaryService summaryService;

    @Autowired
    public void setSummaryService(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping
    public ResponseEntity<SummaryDTO> summaryFromGetDTO(@RequestParam String summary) {

        try {
            SummaryDTO summaryFromDTO = summaryService.setSummary(summary);
            return ResponseEntity.ok(summaryFromDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SummaryDTO("error", e.getMessage()));
        }
    }
}