package com.zhenyu.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/stats")
public class StatsController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/hot-positions")
    public Map<String, Object> getHotPositions(@RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> data = Arrays.asList(
                Map.of("position", "Java开发工程师", "count", 45),
                Map.of("position", "前端开发工程师", "count", 32),
                Map.of("position", "产品经理", "count", 18),
                Map.of("position", "测试工程师", "count", 12)
        );
        return Map.of("code", 200, "data", data);
    }

    @GetMapping("/daily-interviews")
    public Map<String, Object> getDailyInterviews(@RequestParam String begin, @RequestParam String end) {
        LocalDate start = LocalDate.parse(begin, FMT);
        LocalDate endDate = LocalDate.parse(end, FMT);
        Random random = new Random();
        List<Map<String, Object>> data = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(endDate); d = d.plusDays(1)) {
            int total = random.nextInt(15) + 5;
            int completed = random.nextInt(total) + 1;
            double avg = 70 + random.nextDouble() * 20;
            data.add(Map.of(
                    "date", d.format(FMT),
                    "totalSessions", total,
                    "completedSessions", completed,
                    "avgScore", Math.round(avg * 10.0) / 10.0
            ));
        }
        return Map.of("code", 200, "data", data);
    }

    @GetMapping("/score-distribution")
    public Map<String, Object> getScoreDistribution(@RequestParam(defaultValue = "all") String timeRange) {
        List<Map<String, Object>> ranges = Arrays.asList(
                Map.of("range", "0-60", "count", 5),
                Map.of("range", "60-80", "count", 20),
                Map.of("range", "80-100", "count", 10)
        );
        return Map.of("code", 200, "data", Map.of("ranges", ranges));
    }
}
