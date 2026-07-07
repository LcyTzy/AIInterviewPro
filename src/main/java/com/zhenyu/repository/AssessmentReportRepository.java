package com.zhenyu.repository;

import com.zhenyu.entity.AssessmentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AssessmentReportRepository extends JpaRepository<AssessmentReport, Long> {
    Optional<AssessmentReport> findBySessionId(Long sessionId);
}