package com.zhenyu.controller.user;

import com.zhenyu.entity.User;
import com.zhenyu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/center")
@RequiredArgsConstructor
public class UserCenterController {

    private final UserRepository userRepository;

    @GetMapping("/info")
    public Map<String, Object> info(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return Map.of("code", 200, "data", Map.of(
                "id", user.getId(),
                "nickname", user.getNickname(),
                "avatarUrl", user.getAvatarUrl(),
                "phone", user.getPhone()
        ));
    }

    @PutMapping("/info")
    public Map<String, Object> update(@RequestParam Long userId, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(userId).orElseThrow();
        if (body.containsKey("nickname")) user.setNickname(body.get("nickname"));
        if (body.containsKey("avatarUrl")) user.setAvatarUrl(body.get("avatarUrl"));
        userRepository.save(user);
        return Map.of("code", 200, "data", "更新成功");
    }
}