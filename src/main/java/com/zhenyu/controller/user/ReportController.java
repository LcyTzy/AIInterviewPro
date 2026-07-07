package com.zhenyu.controller.user;


import com.zhenyu.entity.AssessmentReport;
import com.zhenyu.repository.AssessmentReportRepository;
import com.zhenyu.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AssessmentReportRepository reportRepository;

    @GetMapping("/{sessionId}")
    public Map<String, Object> getReport(@PathVariable Long sessionId) {
        AssessmentReport report = reportRepository.findBySessionId(sessionId).orElse(null);
        if (report == null) {
            return Map.of("code", 404, "msg", "报告不存在");
        }
        return Map.of("code", 200, "data", report);
    }
}