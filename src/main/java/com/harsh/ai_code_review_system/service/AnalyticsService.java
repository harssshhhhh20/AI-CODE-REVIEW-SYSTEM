package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.dto.TopProblemFileResponse;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.ReviewCommentRepository;
import com.harsh.ai_code_review_system.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepo userRepo;

    public List<TopProblemFileResponse> getTopProblemFiles() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return reviewCommentRepository
                .findTopProblemFilesByUserId(user.getId())
                .stream()
                .limit(5)
                .toList();
    }

    public List<TopProblemFileResponse> getTopProblemFiles(
            Long repositoryId
    ) {
        return reviewCommentRepository
                .findTopProblemFilesByRepositoryId(repositoryId)
                .stream()
                .limit(5)
                .toList();
    }
}