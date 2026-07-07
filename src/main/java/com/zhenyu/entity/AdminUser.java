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
@Table(name = "admin_users")
public class AdminUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false, length = 50) private String username;
    @Column(nullable = false, length = 255) private String password;
    @Column(nullable = false, length = 50) private String name;
    @Column(length = 20) private String phone;
    @Column(length = 500) private String avatar;
    private String role = "OPERATOR";
    private Boolean isActive = true;
    private LocalDateTime lastLoginTime;
    @CreationTimestamp @Column(updatable = false) private LocalDateTime createTime;
    @UpdateTimestamp private LocalDateTime updateTime;
}