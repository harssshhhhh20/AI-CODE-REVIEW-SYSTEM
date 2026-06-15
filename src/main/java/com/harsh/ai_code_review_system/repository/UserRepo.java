package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
