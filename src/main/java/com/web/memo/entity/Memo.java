package com.web.memo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "memo")
@Getter
@EntityListeners(value = { AuditingEntityListener.class })
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(name = "title", length= 255)
    private String title;

    @Column(name="content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @OneToOne(mappedBy = "memo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Summary summary;

    // JPA를 위한 기본 생성자 추가
    protected Memo() {}
    private Memo(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Memo of(String title, String content, User user) {
        return new Memo(title, content, user);
    }

    public void updateMemo(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public void setSummary(Summary summary) {
        this.summary = summary;
    }

}
