package com.random.app.repository;

import com.random.app.entity.Decision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
    Page<Decision> findByUserIdOrderByDecidedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT d.chosenOption.id FROM Decision d WHERE d.user.id = :userId AND d.category.id = :catId AND d.decidedAt >= :since")
    List<Long> findRecentChosenOptionIds(@Param("userId") Long userId, @Param("catId") Long catId, @Param("since") LocalDateTime since);
}
