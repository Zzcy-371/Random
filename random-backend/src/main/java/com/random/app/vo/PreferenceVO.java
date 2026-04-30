package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PreferenceVO {
    private String tag;
    private BigDecimal weight;
}
