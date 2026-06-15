package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;

    @PostMapping("/{userId}")
    public CodeRepository createRepository(
            @PathVariable Long userId,
            @RequestBody CodeRepository repository){

        return repositoryService.createRepository(
                userId,
                repository
        );
    }
}