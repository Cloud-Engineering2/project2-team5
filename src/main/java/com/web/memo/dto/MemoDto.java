package com.web.memo.dto;

import com.web.memo.entity.Memo;
import com.web.memo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
// 임시로 u_id 세팅 때문에 setter 설정 -> 나중에 지우기
@Setter
@AllArgsConstructor
public class MemoDto {

    private Long id;

    private String title;

    private String content;

    private Long userId;

    private String summary;

    private LocalDateTime createdDate;

    public static MemoDto fromEntity(Memo memo) {

        // Summary가 null일 경우도 처리
        String summaryText = (memo.getSummary() != null) ? memo.getSummary().getSummary() : null;
        return new MemoDto(
                memo.getId(),
                memo.getTitle(),
                memo.getContent(),
                memo.getUser().getId(),
                summaryText,
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
