package com.zhenyu.controller.user;

import com.zhenyu.entity.Resume;
import com.zhenyu.repository.ResumeRepository;
import com.zhenyu.service.OssService;
import com.zhenyu.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeParserService parserService;
    private final ResumeRepository resumeRepository;
    private final OssService ossService;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userId") Long userId) {
        try {
            // 上传到 OSS
            String fileUrl = ossService.upload(file, "resumes");

            // 解析简历文本
            String text = parserService.extractText(file);

            // 提取结构化数据（失败时降级）
            Map<String, Object> structured;
            try {
                structured = parserService.extractStructuredData(text);
            } catch (Exception e) {
                structured = Map.of("rawTextPreview", text.substring(0, Math.min(200, text.length())));
            }

            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setFileName(file.getOriginalFilename());
            resume.setFileUrl(fileUrl);
            resume.setFileSize(file.getSize());
            resume.setParsedText(text);
            resume.setStructuredData(structured);
            resume = resumeRepository.save(resume);

            return Map.of("code", 200, "data", Map.of(
                    "resumeId", resume.getId(),
                    "fileUrl", fileUrl,
                    "parsedData", structured,
                    "parsedTextPreview", text.substring(0, Math.min(100, text.length()))
            ));
        } catch (Exception e) {
            return Map.of("code", 500, "msg", "上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    public Map<String, Object> latest(@RequestParam Long userId) {
        Resume resume = resumeRepository.findTopByUserIdOrderByCreateTimeDesc(userId).orElse(null);
        return Map.of("code", 200, "data", resume);
    }
}
