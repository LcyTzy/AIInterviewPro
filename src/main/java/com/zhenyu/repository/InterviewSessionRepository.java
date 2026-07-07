package com.zhenyu.repository;

import com.zhenyu.entity.InterviewSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUserId(Long userId);

    Page<InterviewSession> findByUserId(Long userId, Pageable pageable);

    Page<InterviewSession> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}