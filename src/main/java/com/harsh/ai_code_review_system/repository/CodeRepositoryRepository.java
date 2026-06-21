package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.CodeRepository;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeRepositoryRepository extends JpaRepository<CodeRepository, Long> {
    Optional<CodeRepository> findByNameAndOwner(
            String name,
            String owner
    );
}
