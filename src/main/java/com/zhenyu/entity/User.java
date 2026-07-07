package com.zhenyu.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, length = 64) private String openid;
    @Column(unique = true, length = 20) private String phone;
    @Column(length = 50, nullable = false) private String nickname = "求职者";
    @Column(length = 500) private String avatarUrl;
    private Short gender = 0;
    private Boolean isActive = true;
    private LocalDateTime lastLoginTime;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
    @UpdateTimestamp private LocalDateTime updateTime;
}