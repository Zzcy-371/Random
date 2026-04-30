package com.random.app.service;

import com.random.app.dto.DecideRequestDTO;
import com.random.app.vo.DecisionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DecisionService {
    DecisionVO decide(Long userId, Long categoryId, DecideRequestDTO dto);
    Page<DecisionVO> getHistory(Long userId, Pageable pageable);
    DecisionVO getDecision(Long userId, Long decisionId);
}
