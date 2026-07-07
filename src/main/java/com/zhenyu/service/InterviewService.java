package com.zhenyu.service;
import com.zhenyu.entity.InterviewMessage;
import com.zhenyu.repository.InterviewMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewService {

    private final DeepSeekService deepSeekService;
    private final InterviewMessageRepository messageRepository;

    @Transactional
    public String startInterview(Long sessionId, String resumeText, String position) {
        String firstQuestion;
        try {
            String systemPrompt = """
                    你是一位资深技术面试官。根据候选人的简历和岗位要求，提出第一个技术面试问题。
                    问题应基于简历内容，考察候选人的项目经验和技能深度。
                    只输出问题内容，不要输出额外解释。
                    """;
            String userPrompt = String.format("简历：%s\n岗位：%s", resumeText, position);
            firstQuestion = deepSeekService.chatSync(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.warn("AI生成问题失败，使用默认问题: {}", e.getMessage());
            firstQuestion = String.format("您好！欢迎参加%s岗位的面试。请先简单介绍一下您自己，包括您的教育背景、工作经验和技术栈。", position);
        }

        InterviewMessage msg = new InterviewMessage();
        msg.setSessionId(sessionId);
        msg.setRole("assistant");
        msg.setContent(firstQuestion);
        msg.setIsQuestion(true);
        messageRepository.save(msg);
        return firstQuestion;
    }

    public Flux<String> generateNextQuestionStream(Long sessionId, String userAnswer) {
        List<InterviewMessage> history = messageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", """
                你是一位资深技术面试官。根据候选人的回答，提出一个追问或下一个面试问题。
                问题必须基于候选人的简历和之前的对话，体现技术深度。
                只输出问题内容，不要输出其他文字。
                """));
        for (InterviewMessage msg : history) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }
        messages.add(Map.of("role", "user", "content", userAnswer));

        InterviewMessage userMsg = new InterviewMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(userAnswer);
        userMsg.setIsQuestion(false);
        messageRepository.save(userMsg);

        try {
            return deepSeekService.chatStreamWithHistory(messages);
        } catch (Exception e) {
            log.warn("AI生成追问失败: {}", e.getMessage());
            return Flux.just("感谢您的回答。请继续介绍一下您在项目中的具体职责和贡献。");
        }
    }
}