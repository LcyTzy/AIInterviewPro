package com.zhenyu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "interview_messages")
public class InterviewMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long sessionId;
    @Column(nullable = false, length = 10) private String role;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    private Boolean isQuestion = false;
    private Integer tokensUsed = 0;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
}