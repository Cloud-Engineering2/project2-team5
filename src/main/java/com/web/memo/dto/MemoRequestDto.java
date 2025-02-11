package com.web.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestDto {

    private Long id;

    private String title;

    private String content;

    private String summary;

    private MemoRequestDto (Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public static MemoRequestDto fromDtoWithNoSummary(MemoDto memoDto) {
        return new MemoRequestDto(
                memoDto.getId(),
                memoDto.getTitle(),
                memoDto.getContent()
                );
    }

    public static MemoRequestDto fromDtoWithSummary(MemoDto memoDto, String summaryText) {
        return new MemoRequestDto(
                memoDto.getId(),
                memoDto.getTitle(),
                memoDto.getContent(),
                summaryText
        );
    }

    public MemoDto toDto() {
        return new MemoDto(this.title, this.content);
    }
}
