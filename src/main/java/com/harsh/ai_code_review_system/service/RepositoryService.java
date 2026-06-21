package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.dto.RepositoryResponse;
import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.CodeRepositoryRepository;
import com.harsh.ai_code_review_system.repository.ReviewRepository;
import com.harsh.ai_code_review_system.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final CodeRepositoryRepository repositoryRepository;
    private final UserRepo userRepo;
    private final ReviewRepository reviewRepository;

    public CodeRepository createRepository(Long userId, CodeRepository repository) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        repository.setUser(user);

        return repositoryRepository.save(repository);
    }

    public long getTotalRepositories() {
        return repositoryRepository.count();
    }

    public List<RepositoryResponse> getRepositories() {

        return repositoryRepository.findAll()
                .stream()
                .map(repository -> new RepositoryResponse(
                        repository.getId(),
                        repository.getName(),
                        repository.getOwner(),
                        repository.getStatus()
                ))
                .toList();
    }
}