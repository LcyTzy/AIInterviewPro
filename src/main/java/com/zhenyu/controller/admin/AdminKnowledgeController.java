package com.zhenyu.controller.admin;

import com.zhenyu.entity.KnowledgeDoc;
import com.zhenyu.repository.KnowledgeDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/knowledge")
@RequiredArgsConstructor
public class AdminKnowledgeController {

    private final KnowledgeDocRepository knowledgeDocRepository;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<KnowledgeDoc> result = knowledgeDocRepository.findByTitleContainingAndCategory(title, category, pr);
        return Map.of("code", 200, "data", Map.of(
                "records", result.getContent(),
                "total", result.getTotalElements()
        ));
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody KnowledgeDoc doc) {
        knowledgeDocRepository.save(doc);
        return Map.of("code", 200, "msg", "创建成功");
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody KnowledgeDoc doc) {
        KnowledgeDoc existing = knowledgeDocRepository.findById(doc.getId())
                .orElseThrow(() -> new RuntimeException("文档不存在"));
        existing.setTitle(doc.getTitle());
        existing.setContent(doc.getContent());
        existing.setCategory(doc.getCategory());
        existing.setTags(doc.getTags());
        knowledgeDocRepository.save(existing);
        return Map.of("code", 200, "msg", "更新成功");
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestParam Long id) {
        knowledgeDocRepository.deleteById(id);
        return Map.of("code", 200, "msg", "删除成功");
    }

    @PostMapping("/status/{status}")
    public Map<String, Object> updateStatus(@RequestParam Long id, @PathVariable int status) {
        KnowledgeDoc doc = knowledgeDocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档不存在"));
        doc.setIsActive(status == 1);
        knowledgeDocRepository.save(doc);
        return Map.of("code", 200, "msg", "操作成功");
    }
}
