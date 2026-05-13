package com.yinyang.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MultiTrendDataResponse {
    private List<String> labels;
    private Map<String, List<BigDecimal>> datasets;
}
