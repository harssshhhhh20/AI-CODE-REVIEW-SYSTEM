package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.CreateUserRequest;
import com.harsh.ai_code_review_system.dto.UserResponse;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getAllUser();
    }
}
