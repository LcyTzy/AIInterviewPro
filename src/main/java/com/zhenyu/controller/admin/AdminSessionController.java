package com.zhenyu.controller.admin;

import com.zhenyu.entity.AssessmentReport;
import com.zhenyu.entity.InterviewMessage;
import com.zhenyu.entity.InterviewSession;
import com.zhenyu.entity.User;
import com.zhenyu.repository.AssessmentReportRepository;
import com.zhenyu.repository.InterviewMessageRepository;
import com.zhenyu.repository.InterviewSessionRepository;
import com.zhenyu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/session")
@RequiredArgsConstructor
public class AdminSessionController {

    private final InterviewSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AssessmentReportRepository reportRepository;
    private final InterviewMessageRepository messageRepository;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String userName,
            @RequestParam(defaultValue = "") String jobPosition,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<InterviewSession> result = sessionRepository.findAll(pr);

        var userIds = result.getContent().stream()
                .map(InterviewSession::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userRepository.findAllById(userIds).forEach(u -> userMap.put(u.getId(), u.getNickname()));
        }

        var records = result.getContent().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("userId", s.getUserId());
            map.put("userName", userMap.getOrDefault(s.getUserId(), "未知用户"));
            map.put("jobPosition", s.getJobPosition());
            map.put("difficulty", s.getDifficulty());
            map.put("status", s.getStatus());
            map.put("questionCount", s.getQuestionCount());
            map.put("startTime", s.getStartTime());
            map.put("endTime", s.getEndTime());
            reportRepository.findBySessionId(s.getId())
                    .ifPresent(r -> map.put("overallScore", r.getOverallScore()));
            return map;
        }).collect(Collectors.toList());

        return Map.of("code", 200, "data", Map.of(
                "records", records,
                "total", result.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        InterviewSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        Map<String, Object> result = new HashMap<>();
        result.put("id", session.getId());
        result.put("userId", session.getUserId());
        result.put("jobPosition", session.getJobPosition());
        result.put("difficulty", session.getDifficulty());
        result.put("status", session.getStatus());
        result.put("questionCount", session.getQuestionCount());
        result.put("startTime", session.getStartTime());
        result.put("endTime", session.getEndTime());

        userRepository.findById(session.getUserId())
                .ifPresent(u -> result.put("userName", u.getNickname()));
        reportRepository.findBySessionId(id)
                .ifPresent(r -> {
                    result.put("overallScore", r.getOverallScore());
                    result.put("scoreLogic", r.getScoreLogic());
                    result.put("scoreDepth", r.getScoreDepth());
                    result.put("scoreMatch", r.getScoreMatch());
                    result.put("scoreCommunication", r.getScoreCommunication());
                    result.put("improvementSuggestions", r.getImprovementSuggestions());
                });

        List<Map<String, Object>> messages = messageRepository.findBySessionIdOrderByCreateTimeAsc(id)
                .stream().map(m -> Map.<String, Object>of(
                        "role", m.getRole(),
                        "content", m.getContent(),
                        "isQuestion", m.getIsQuestion(),
                        "createTime", m.getCreateTime()
                )).collect(Collectors.toList());
        result.put("messages", messages);

        return Map.of("code", 200, "data", result);
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestParam Long id) {
        messageRepository.deleteAll(messageRepository.findBySessionIdOrderByCreateTimeAsc(id));
        reportRepository.findBySessionId(id).ifPresent(r -> reportRepository.delete(r));
        sessionRepository.deleteById(id);
        return Map.of("code", 200, "msg", "删除成功");
    }
}
