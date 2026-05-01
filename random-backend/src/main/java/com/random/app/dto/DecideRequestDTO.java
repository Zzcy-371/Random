package com.random.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class DecideRequestDTO {
    private List<String> preferenceFilters;
    private Boolean excludeRecent = true;
    private Integer excludeRecentDays = 3;
    private List<Long> optionIds;
}
