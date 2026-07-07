package com.zhenyu.service;

import com.zhenyu.entity.AssessmentReport;
import com.zhenyu.entity.InterviewMessage;
import com.zhenyu.repository.AssessmentReportRepository;
import com.zhenyu.repository.InterviewMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final DeepSeekService deepSeekService;
    private final InterviewMessageRepository messageRepository;
    private final AssessmentReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    @Async("reportExecutor")
    @Transactional
    public CompletableFuture<AssessmentReport> generateReport(Long sessionId) {
        log.info("开始生成报告: sessionId={}", sessionId);
        List<InterviewMessage> messages = messageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
        StringBuilder transcript = new StringBuilder();
        for (InterviewMessage msg : messages) {
            transcript.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }

        String systemPrompt = """
                你是一位专业的面试评估师。请根据对话记录，从以下四个维度打分（0-100）：
                1. 逻辑清晰度
                2. 技术深度
                3. 岗位匹配度
                4. 表达能力
                并给出总体评价和改进建议。以JSON格式返回：
                {
                  "scoreLogic": 整数,
                  "scoreDepth": 整数,
                  "scoreMatch": 整数,
                  "scoreCommunication": 整数,
                  "overallScore": 整数,
                  "improvementSuggestions": "建议文本",
                  "detailedFeedback": { "维度名": "详细评语" }
                }
                """;
        String userPrompt = "对话记录：\n" + transcript;

        String jsonResult = deepSeekService.chatSync(systemPrompt, userPrompt);
        try {
            Map<String, Object> result = objectMapper.readValue(jsonResult, Map.class);
            AssessmentReport report = new AssessmentReport();
            report.setSessionId(sessionId);
            report.setOverallScore(((Number) result.get("overallScore")).doubleValue());
            report.setScoreLogic(((Number) result.get("scoreLogic")).doubleValue());
            report.setScoreDepth(((Number) result.get("scoreDepth")).doubleValue());
            report.setScoreMatch(((Number) result.get("scoreMatch")).doubleValue());
            report.setScoreCommunication(((Number) result.get("scoreCommunication")).doubleValue());
            report.setImprovementSuggestions((String) result.get("improvementSuggestions"));
            report.setDetailedFeedback((Map) result.get("detailedFeedback"));
            report.setGenerateStatus("completed");
            reportRepository.save(report);
            log.info("报告生成完成: sessionId={}", sessionId);
            return CompletableFuture.completedFuture(report);
        } catch (Exception e) {
            log.error("报告生成失败", e);
            throw new RuntimeException("报告解析失败", e);
        }
    }
}