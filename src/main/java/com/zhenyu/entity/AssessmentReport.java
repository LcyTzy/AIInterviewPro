package com.zhenyu.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "assessment_reports")
public class AssessmentReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private Long sessionId;
    private Double overallScore;
    private Double scoreLogic;
    private Double scoreDepth;
    private Double scoreMatch;
    private Double scoreCommunication;
    @Column(columnDefinition = "TEXT") private String improvementSuggestions;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "JSONB")
    private Map<String, Object> detailedFeedback;
    private String generateStatus = "pending";
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
    @UpdateTimestamp private LocalDateTime updateTime;
}