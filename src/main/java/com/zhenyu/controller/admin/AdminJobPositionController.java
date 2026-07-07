package com.zhenyu.controller.admin;

import com.zhenyu.entity.JobPosition;
import com.zhenyu.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/job-position")
@RequiredArgsConstructor
public class AdminJobPositionController {

    private final JobPositionRepository repository;

    @GetMapping("/page")
    public Map<String, Object> page(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "") String category,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pr = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "sort"));
        Page<JobPosition> result;
        if (name.isEmpty() && category.isEmpty()) {
            result = repository.findAll(pr);
        } else {
            result = repository.findByNameContainingAndCategoryContaining(
                    name.isEmpty() ? "" : name,
                    category.isEmpty() ? "" : category,
                    pr
            );
        }
        return Map.of("code", 200, "data", result);
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        List<JobPosition> list = repository.findByIsActiveTrueOrderBySortAsc();
        return Map.of("code", 200, "data", list);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody JobPosition position) {
        return Map.of("code", 200, "data", repository.save(position));
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody JobPosition position) {
        return Map.of("code", 200, "data", repository.save(position));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return Map.of("code", 200);
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        JobPosition p = repository.findById(id).orElseThrow();
        p.setIsActive(body.get("status") == 1);
        return Map.of("code", 200, "data", repository.save(p));
    }
}
