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
@Table(name = "knowledge_docs")
public class KnowledgeDoc {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Column(length = 50) private String category;
    @Column(columnDefinition = "varchar(255)[]") private String[] tags;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "JSONB")
    private Map<String, Object> metadata;
    private Boolean isActive = true;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
    @UpdateTimestamp private LocalDateTime updateTime;
}