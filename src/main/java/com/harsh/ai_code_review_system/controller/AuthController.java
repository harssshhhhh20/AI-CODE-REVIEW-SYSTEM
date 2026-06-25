package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.auth.AuthResponse;
import com.harsh.ai_code_review_system.dto.auth.LoginRequest;
import com.harsh.ai_code_review_system.dto.auth.RegisterRequest;
import com.harsh.ai_code_review_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}