package com.random.app.repository;

import com.random.app.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserIdAndCategoryIdAndTag(Long userId, Long categoryId, String tag);
    List<UserPreference> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
