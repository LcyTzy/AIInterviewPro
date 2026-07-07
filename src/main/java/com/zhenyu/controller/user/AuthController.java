package com.zhenyu.controller.user;

import com.zhenyu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String token = authService.wechatLogin(code);
        return Map.of("code", 200, "data", Map.of("token", token));
    }

    @PostMapping("/loginByPhone")
    public Map<String, Object> loginByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String token = authService.phoneLogin(phone, code);
        return Map.of("code", 200, "data", Map.of("token", token));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return Map.of("code", 200, "data", "登出成功");
    }
}