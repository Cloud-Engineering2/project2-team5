package com.web.memo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid")
    private Long sid;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
}
