package com.zhenyu.repository;

import com.zhenyu.entity.JobPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    Page<JobPosition> findByNameContainingAndCategoryContaining(String name, String category, Pageable pageable);
    List<JobPosition> findByIsActiveTrueOrderBySortAsc();
}
