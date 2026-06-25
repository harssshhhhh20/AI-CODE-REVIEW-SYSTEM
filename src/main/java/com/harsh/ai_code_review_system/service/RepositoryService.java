package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.dto.RepositoryResponse;
import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.CodeRepositoryRepository;
import com.harsh.ai_code_review_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final CodeRepositoryRepository repositoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public CodeRepository createRepository(
            CodeRepository repository
    ) {

        User user = userService.getCurrentAuthenticatedUser();

        repository.setUser(user);

        return repositoryRepository.save(repository);
    }

    public long getTotalRepositories() {

        User user = userService.getCurrentAuthenticatedUser();

        return repositoryRepository
                .findByUserId(user.getId())
                .size();
    }

    public List<RepositoryResponse> getRepositories() {

        User user = userService.getCurrentAuthenticatedUser();

        return repositoryRepository
                .findByUserId(user.getId())
                .stream()
                .map(repository -> new RepositoryResponse(
                        repository.getId(),
                        repository.getName(),
                        repository.getOwner(),
                        repository.getStatus()
                ))
                .toList();
    }

    public CodeRepository getOwnedRepository(
            Long repositoryId
    ) {

        CodeRepository repository =
                repositoryRepository.findById(repositoryId)
                        .orElseThrow(() ->
                                new RuntimeException("Repository not found"));

        User currentUser =
                userService.getCurrentAuthenticatedUser();

        if (!repository.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException("Access denied");
        }

        return repository;
    }

    public void deleteRepository(Long id) {

        User user =
                userService.getCurrentAuthenticatedUser();

        CodeRepository repository =
                repositoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Repository not found"));

        if (!repository.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        repositoryRepository.delete(repository);
    }
}