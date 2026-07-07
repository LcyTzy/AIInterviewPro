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
@Table(name = "resumes")
public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 255) private String fileName;
    @Column(nullable = false, length = 500) private String fileUrl;
    private Long fileSize;
    @Column(columnDefinition = "TEXT") private String parsedText;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "JSONB")
    private Map<String, Object> structuredData;
    private Boolean isActive = true;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
    @UpdateTimestamp private LocalDateTime updateTime;
}