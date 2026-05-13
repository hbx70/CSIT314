package com.yinyang.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataResponse {
    private List<String> labels;
    private List<BigDecimal> values;
}
