package com.web.memo.dto;

import com.web.memo.entity.Summary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {

    private Long id;

    private Long memoId;

    private String summary;

    private LocalDateTime createdDate;

    public static SummaryDTO fromEntity(Summary summary) {
        return new SummaryDTO(
                summary.getId(),
                summary.getMemo().getId(),
                summary.getSummaryText(),
                summary.getCreatedDate()
        );
    }
    public void setSummaryToFileName(String fileName) {
        this.summary = fileName;
    }

}
