package com.web.memo.repository;

import com.web.memo.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {

    List<Summary> findBySummary(String summary);

    List<Summary> findBySummaryContaining(String keyword);

    @Query("SELECT s FROM Summary s WHERE s.memo.id = :memoId")
    Summary findByMemoId(@Param("memoId") Long memoId);

}
