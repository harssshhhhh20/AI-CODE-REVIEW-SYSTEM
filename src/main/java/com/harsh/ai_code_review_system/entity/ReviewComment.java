package com.harsh.ai_code_review_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String severity;

    private String fileName;

    private String lineNumber;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;
}