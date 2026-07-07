package com.zhenyu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "interview_sessions")
public class InterviewSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    private Long resumeId;
    @Column(nullable = false, length = 100) private String jobPosition;
    private String difficulty = "medium";
    private String status = "ongoing";
    private Integer questionCount = 0;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime startTime;
    private LocalDateTime endTime;
}