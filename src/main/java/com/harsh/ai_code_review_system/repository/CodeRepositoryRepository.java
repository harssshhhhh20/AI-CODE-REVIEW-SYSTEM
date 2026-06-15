package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.CodeRepository;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepositoryRepository extends JpaRepository<CodeRepository, Long> {
}
