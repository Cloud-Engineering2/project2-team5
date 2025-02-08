package com.web.memo.service;

import com.web.memo.entity.Summary;
import com.web.memo.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final SummaryRepository summaryRepository;

    @Autowired
    public DatabaseService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public void saveSummary(String summaryText) {
        Summary summary = new Summary(summaryText);
        summaryRepository.save(summary);
        System.out.println("Summary saved to database");
    }
}
