package com.zhenyu.controller.admin;

import com.zhenyu.entity.AdminUser;
import com.zhenyu.repository.AdminUserRepository;
import com.zhenyu.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = JwtUtil.generateToken(admin.getId().toString());
        return Map.of("code", 200, "data", Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "name", admin.getName(),
                "token", token
        ));
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of("code", 200, "data", Map.of(
                "name", "Super Admin",
                "role", "admin"
        ));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return Map.of("code", 200, "msg", "退出成功");
    }

    @PutMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        AdminUser admin = adminUserRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(admin);
        return Map.of("code", 200, "msg", "密码修改成功");
    }
}