package com.web.memo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "memo", nullable = false)
    private Memo memo;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    protected Summary() {}
    private Summary(Memo memo, String summary) {
        this.memo = memo;
        this.summary = summary;
    }

    public static Summary of(Memo memo, String summary) {
        return new Summary(memo, summary);
    }

    public void updateContent(String summary) {
        this.summary = summary;
    }
}

