package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.UserRepo;
import com.harsh.ai_code_review_system.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepo userRepo;
    public User createUser(User user){
        return userRepo.save(user);
    }
    public List<User> getAllUser(){
        return userRepo.findAll();
    }
}
