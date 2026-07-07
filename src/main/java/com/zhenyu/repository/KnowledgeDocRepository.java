package com.zhenyu.repository;

import com.zhenyu.entity.KnowledgeDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeDocRepository extends JpaRepository<KnowledgeDoc, Long> {
    Page<KnowledgeDoc> findByTitleContainingAndCategory(String title, String category, Pageable pageable);

    @Query("SELECT k FROM KnowledgeDoc k WHERE k.isActive = true AND (LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KnowledgeDoc> searchByKeyword(@Param("keyword") String keyword);
}