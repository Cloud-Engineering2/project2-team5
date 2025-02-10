package com.web.memo.repository;

import com.web.memo.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Summary findByMemoId(Long memoId);
}
