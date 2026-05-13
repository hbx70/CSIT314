package com.yinyang.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private BigDecimal totalDonationAmount;
    private Integer totalUsers;
    private Integer totalFra;
    private Integer totalDonations;
}