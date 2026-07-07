package com.zhenyu.repository;

import com.zhenyu.entity.InterviewMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewMessageRepository extends JpaRepository<InterviewMessage, Long> {
    List<InterviewMessage> findBySessionIdOrderByCreateTimeAsc(Long sessionId);
}