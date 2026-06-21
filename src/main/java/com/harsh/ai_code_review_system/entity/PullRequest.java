package com.harsh.ai_code_review_system.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pull_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long githubPrId;

    private Integer prNumber;

    private String title;

    private String author;

    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private CodeRepository repository;

    @OneToMany(mappedBy = "pullRequest")
    private List<Review> reviews;

    @Column(columnDefinition = "TEXT")
    private String diff;
}
