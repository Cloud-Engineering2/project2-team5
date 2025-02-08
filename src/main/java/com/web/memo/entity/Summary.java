package com.web.memo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    public Summary(String summaryText) {
        this.summaryText = summaryText;
    }
}
