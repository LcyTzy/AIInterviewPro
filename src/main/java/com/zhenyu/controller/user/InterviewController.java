package com.zhenyu.controller.user;

import com.zhenyu.entity.InterviewSession;
import com.zhenyu.repository.InterviewSessionRepository;
import com.zhenyu.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSessionRepository sessionRepository;

    @GetMapping("/sessions")
    public Map<String, Object> sessions(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<InterviewSession> result = (status == null || status.isEmpty())
                ? sessionRepository.findByUserId(userId, pr)
                : sessionRepository.findByUserIdAndStatus(userId, status, pr);
        var records = result.getContent().stream().map(s -> {
            return Map.of(
                "id", s.getId(),
                "jobPosition", s.getJobPosition(),
                "difficulty", s.getDifficulty(),
                "status", s.getStatus(),
                "questionCount", s.getQuestionCount(),
                "startTime", s.getStartTime().toString(),
                "overallScore", 0
            );
        }).collect(Collectors.toList());
        return Map.of("code", 200, "data", Map.of("records", records, "total", result.getTotalElements()));
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody CreateRequest request) {
        InterviewSession session = new InterviewSession();
        session.setUserId(request.getUserId());
        session.setJobPosition(request.getJobPosition());
        session.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : "medium");
        session.setStatus("ongoing");
        session = sessionRepository.save(session);
        return Map.of("code", 200, "data", Map.of("sessionId", session.getId()));
    }

    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody StartRequest request) {
        String firstQuestion = interviewService.startInterview(
                request.getSessionId(),
                request.getResumeText(),
                request.getJobPosition()
        );
        return Map.of("code", 200, "data", Map.of("sessionId", request.getSessionId(), "firstQuestion", firstQuestion));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam Long sessionId, @RequestParam String answer) {
        return interviewService.generateNextQuestionStream(sessionId, answer)
                .map(chunk -> "data: " + chunk + "\n\n")
                .concatWith(Flux.just("data: [DONE]\n\n"));
    }

    @PostMapping("/end")
    public Map<String, Object> end(@RequestBody EndRequest request) {
        return Map.of("code", 200, "data", Map.of("sessionId", request.getSessionId(), "status", "processing"));
    }

    static class CreateRequest {
        private Long userId;
        private String jobPosition;
        private String difficulty;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getJobPosition() { return jobPosition; }
        public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    }
    static class StartRequest {
        private Long sessionId;
        private String resumeText;
        private String jobPosition;
        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getResumeText() { return resumeText; }
        public void setResumeText(String resumeText) { this.resumeText = resumeText; }
        public String getJobPosition() { return jobPosition; }
        public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }
    }
    static class EndRequest {
        private Long sessionId;
        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    }
}
