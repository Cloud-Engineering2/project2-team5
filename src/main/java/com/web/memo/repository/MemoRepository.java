package com.web.memo.repository;

import com.web.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    // summary 없이 찾아오는 용도
    List<Memo> findByUserId(Long userId);

    // summary 포함해서 찾아오는 용도
    @Query("SELECT m FROM Memo m LEFT JOIN FETCH m.summary WHERE m.id = :mid")
    Optional<Memo> findByIdWithSummary(@Param("mid") Long mid);

}
