package com.zhenyu.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeepSeekService {

    private final WebClient deepSeekWebClient;
    private final ObjectMapper objectMapper;

    @Value("${deepseek.api.model}")
    private String model;

    @Value("${deepseek.api.max-tokens:4096}")
    private Integer maxTokens;

    @Value("${deepseek.api.temperature:0.7}")
    private Double temperature;


    @Autowired
    public DeepSeekService(WebClient deepSeekWebClient, ObjectMapper objectMapper) {
        this.deepSeekWebClient = deepSeekWebClient;
        this.objectMapper = objectMapper;
    }
    public String chatSync(String systemPrompt, String userMessage) {
        ObjectNode request = buildRequest(systemPrompt, userMessage, false);
        return deepSeekWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.path("choices").path(0).path("message").path("content").asText())
                .block();
    }

    public Flux<String> chatStream(String systemPrompt, String userMessage) {
        ObjectNode request = buildRequest(systemPrompt, userMessage, true);
        return deepSeekWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractDeltaFromStreamChunk)
                .filter(text -> text != null && !text.isEmpty());
    }

    public Flux<String> chatStreamWithHistory(List<Map<String, String>> messages) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("stream", true);
        root.put("max_tokens", maxTokens);
        root.put("temperature", temperature);

        ArrayNode msgArray = objectMapper.createArrayNode();
        messages.forEach(msg -> {
            ObjectNode m = msgArray.addObject();
            m.put("role", msg.get("role"));
            m.put("content", msg.get("content"));
        });
        root.set("messages", msgArray);

        return deepSeekWebClient.post()
                .uri("/chat/completions")
                .bodyValue(root)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractDeltaFromStreamChunk)
                .filter(text -> text != null && !text.isEmpty());
    }

    private ObjectNode buildRequest(String system, String user, boolean stream) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("stream", stream);
        root.put("max_tokens", maxTokens);
        root.put("temperature", temperature);

        ArrayNode messages = objectMapper.createArrayNode();
        if (system != null && !system.isEmpty()) {
            messages.addObject().put("role", "system").put("content", system);
        }
        messages.addObject().put("role", "user").put("content", user);
        root.set("messages", messages);
        return root;
    }

    private String extractDeltaFromStreamChunk(String chunk) {
        if (!chunk.startsWith("data: ")) return null;
        String jsonData = chunk.substring(6).trim();
        if ("[DONE]".equals(jsonData)) return null;
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            return node.path("choices").path(0).path("delta").path("content").asText();
        } catch (Exception e) {
            log.warn("解析流式响应失败: {}", chunk, e);
            return null;
        }
    }
}