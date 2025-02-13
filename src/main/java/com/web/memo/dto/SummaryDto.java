package com.web.memo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SummaryDTO {
    private String title;
    private String summary;

    public SummaryDTO(String title, String summary) {
        this.title = title;
        this.summary = summary;


    }
}