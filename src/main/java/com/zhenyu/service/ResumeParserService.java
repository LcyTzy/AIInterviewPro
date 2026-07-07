package com.zhenyu.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeParserService {

    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper;
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file)  throws IOException, TikaException {
        return tika.parseToString(file.getInputStream());
    }

    public Map<String, Object> extractStructuredData(String text) {
        String truncated = text.length() > 3000 ? text.substring(0, 3000) : text;
        String prompt = """
                你是一位专业的简历分析师。请从以下简历文本中提取关键信息，并以JSON格式返回：
                {
                  "name": "姓名",
                  "school": "毕业院校",
                  "major": "专业",
                  "degree": "学历",
                  "skills": ["技能1", "技能2"],
                  "experienceYears": 年数,
                  "projects": ["项目1", "项目2"]
                }
                简历内容：
                """ + truncated;

        String response = deepSeekService.chatSync(
                "你是一个JSON生成器，只返回合法JSON，不要任何额外说明。",
                prompt
        );
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            return Map.of("rawTextPreview", truncated.substring(0, Math.min(200, truncated.length())));
        }
    }
}