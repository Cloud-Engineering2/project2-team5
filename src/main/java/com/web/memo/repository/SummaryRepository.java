package com.web.memo.repository;

import com.web.memo.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {

    List<Summary> findBySummary(String summary);

    List<Summary> findBySummaryContaining(String keyword);

    Summary findByMemoId(Long memoId);
}
