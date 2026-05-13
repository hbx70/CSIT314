package com.yinyang.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FRAProgressResponse {
    private String fraTitle;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
}