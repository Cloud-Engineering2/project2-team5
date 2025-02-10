package com.web.memo.dto;

import com.web.memo.entity.Memo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SummaryDto {

    private Long id;

    private Long memoId;

    private String summary;

    private LocalDateTime createdDate;

    public void setSummary(String summary) {
        this.summary = summary;
    }


}
