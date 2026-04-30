package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DecisionVO {
    private Long decisionId;
    private OptionVO chosenOption;
    private List<OptionVO> alternates;
    private LocalDateTime decidedAt;
}
