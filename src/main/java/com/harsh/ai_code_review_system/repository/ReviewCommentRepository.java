package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.Review;
import com.harsh.ai_code_review_system.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository
        extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByReview(Review review);
    long countBySeverity(String severity);
}