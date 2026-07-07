package com.zhenyu.controller.admin;

import com.zhenyu.entity.User;
import com.zhenyu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String nickname,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(defaultValue = "") String isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> result = userRepository.findAll(pr);
        return Map.of("code", 200, "data", Map.of(
                "records", result.getContent(),
                "total", result.getTotalElements()
        ));
    }

    @PostMapping("/status/{status}")
    public Map<String, Object> updateStatus(@RequestParam Long id, @PathVariable int status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setIsActive(status == 1);
        userRepository.save(user);
        return Map.of("code", 200, "msg", "操作成功");
    }
}
