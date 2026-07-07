package com.zhenyu.service;

import com.zhenyu.entity.User;
import com.zhenyu.repository.UserRepository;
import com.zhenyu.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public String wechatLogin(String code) {
        // 模拟微信登录，实际需调用微信API获取openid
        String openid = "mock_openid_" + code;
        User user = userRepository.findByOpenid(openid).orElseGet(() -> {
            User newUser = new User();
            newUser.setOpenid(openid);
            newUser.setNickname("微信用户");
            return userRepository.save(newUser);
        });
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        return JwtUtil.generateToken(user.getId().toString());
    }

    public String phoneLogin(String phone, String code) {
        // 简单校验验证码（模拟）
        if (!"123456".equals(code)) {
            throw new RuntimeException("验证码错误");
        }
        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setNickname("手机用户");
            return userRepository.save(newUser);
        });
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        return JwtUtil.generateToken(user.getId().toString());
    }
}
