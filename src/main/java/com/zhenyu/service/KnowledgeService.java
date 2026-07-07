package com.zhenyu.service;

import com.zhenyu.entity.KnowledgeDoc;
import com.zhenyu.repository.KnowledgeDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final DeepSeekService deepSeekService;
    private final KnowledgeDocRepository knowledgeDocRepository;

    // 简化版：使用关键词检索（实际可扩展为向量检索）
    public List<KnowledgeDoc> searchSimilarDocs(String question, int topK) {
        return knowledgeDocRepository.searchByKeyword(question).stream().limit(topK).collect(Collectors.toList());
    }

    public Flux<String> askQuestion(String question) {
        List<KnowledgeDoc> docs = searchSimilarDocs(question, 5);
        String context = docs.stream().map(KnowledgeDoc::getContent).collect(Collectors.joining("\n---\n"));
        String systemPrompt = "你是一个技术问答助手，参考以下知识库内容回答问题。如果知识库没有相关信息，请基于你的知识回答。";
        String userPrompt = "知识库内容：\n" + context + "\n\n问题：" + question;
        return deepSeekService.chatStream(systemPrompt, userPrompt);
    }
}