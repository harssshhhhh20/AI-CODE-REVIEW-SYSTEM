package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.service.UserService;
import lombok.RequiredArgsConstructor;
import com.harsh.ai_code_review_system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getAllUser();
    }
}
