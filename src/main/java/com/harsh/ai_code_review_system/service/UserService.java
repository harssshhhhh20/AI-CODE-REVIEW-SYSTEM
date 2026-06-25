package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.dto.CreateUserRequest;
import com.harsh.ai_code_review_system.dto.UserResponse;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.UserRepo;
import com.harsh.ai_code_review_system.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepo userRepo;
    public UserResponse createUser(CreateUserRequest request){
        User user = User.builder()
                .githubId(request.githubId())
                .username(request.username())
                .email(request.email())
                .build();

        User savedUser = userRepo.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }
    public List<User> getAllUser(){
        return userRepo.findAll();
    }

    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public User getCurrentAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}
