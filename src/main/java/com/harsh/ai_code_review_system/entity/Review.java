package com.harsh.ai_code_review_system.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer qualityScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pull_request_id")
    private PullRequest pullRequest;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewComment> comments;

    @Column(nullable = false)
    private String diffHash;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    private Long responseTimeMs;

    private String modelName;

    private Boolean cacheHit;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
