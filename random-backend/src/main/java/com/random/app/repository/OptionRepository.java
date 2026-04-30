package com.random.app.repository;

import com.random.app.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByUserIdAndCategoryIdAndIsActiveTrue(Long userId, Long categoryId);

    @Query("SELECT o FROM Option o WHERE o.user.id = :userId AND o.category.id = :catId AND o.isActive = true AND o.id NOT IN :excludeIds")
    List<Option> findActiveExcluding(@Param("userId") Long userId, @Param("catId") Long catId, @Param("excludeIds") List<Long> excludeIds);
}
