package com.web.memo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Table(name = "summary")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    
    @OneToOne
    @JoinColumn(name = "memo", nullable = false)
    private Memo memo;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    protected Summary() {}
    private Summary(Memo memo, String summary) {
        this.memo = memo;
        this.summaryText = summary;
    }

    public static Summary of(Memo memo, String summary) {
        return new Summary(memo, summary);
    }

    public void updateContent(String summary) {
        this.summaryText = summary;
    }
}
