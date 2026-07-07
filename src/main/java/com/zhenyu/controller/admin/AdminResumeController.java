package com.zhenyu.controller.admin;

import com.zhenyu.entity.Resume;
import com.zhenyu.entity.User;
import com.zhenyu.repository.ResumeRepository;
import com.zhenyu.repository.UserRepository;
import com.zhenyu.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/resume")
@RequiredArgsConstructor
public class AdminResumeController {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final OssService ossService;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String userName,
            @RequestParam(defaultValue = "") String fileName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Resume> result = resumeRepository.findAll(pr);

        var userIds = result.getContent().stream()
                .map(Resume::getUserId)
                .collect(Collectors.toSet());

        Map<Long, String> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userRepository.findAllById(userIds).forEach(u -> userMap.put(u.getId(), u.getNickname()));
        }

        var records = result.getContent().stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("userId", r.getUserId());
            map.put("userName", userMap.getOrDefault(r.getUserId(), "未知用户"));
            map.put("fileName", r.getFileName());
            map.put("fileUrl", r.getFileUrl());
            map.put("createTime", r.getCreateTime());
            return map;
        }).collect(Collectors.toList());

        return Map.of("code", 200, "data", Map.of(
                "records", records,
                "total", result.getTotalElements()
        ));
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestParam Long id) {
        Resume resume = resumeRepository.findById(id).orElse(null);
        if (resume != null) {
            ossService.delete(resume.getFileUrl());
            resumeRepository.deleteById(id);
        }
        return Map.of("code", 200, "msg", "删除成功");
    }
}
