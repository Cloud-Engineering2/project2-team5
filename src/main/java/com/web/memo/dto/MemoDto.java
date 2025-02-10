package com.web.memo.dto;

import com.web.memo.entity.Memo;
import com.web.memo.entity.Summary;
import com.web.memo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemoDto {

    private Long id;

    private String title;

    private String content;

    private Long userId;

    private Long summaryId;

    private LocalDateTime createdDate;

    public void setContentToFileName(String fileName) {
        this.content = fileName;
    }

    public static MemoDto fromEntity(Memo memo) {

        // Summary가 null일 경우도 처리
        Long summaryId = (memo.getSummary() != null) ? memo.getSummary().getId() : null;
        return new MemoDto(
                memo.getId(),
                memo.getTitle(),
                memo.getContent(),
                memo.getUser().getId(),
                summaryId,
                memo.getCreatedDate()
        );
    }

    public Memo toEntity(User user) {
        return Memo.of(
                this.title,
                this.content,
                user
        );
    }
}
