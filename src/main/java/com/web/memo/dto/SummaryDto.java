package com.web.memo.dto;

import com.web.memo.entity.Memo;
import com.web.memo.entity.Summary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {

    private Long id;

    private Long memoId;

    private String summary;

    private LocalDateTime createdDate;

    public SummaryDto(String summaryText) {
        this.summary = summaryText;
    }

    public static SummaryDto fromEntity(Summary summary) {
        return new SummaryDto(
                summary.getId(),
                summary.getMemo().getId(),
                summary.getSummary(),
                summary.getCreatedDate()
        );
    }

    public Summary toEntity(Memo memo) {
        return Summary.of(
                this.summary,
                memo
        );
    }

    public static SummaryDto of(String summaryText) {
        return new SummaryDto(summaryText);
    }
    public void setSummary(String fileName) {
        this.summary = fileName;
    }



}

