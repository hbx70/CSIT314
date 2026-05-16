package com.yinyang.project.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Report {
    private TrendDataResponse donationAmountTrend;
    private TrendDataResponse donationCountTrend;
    private TrendDataResponse donationAvgTrend;
    private TopDataResponse<TopData> donationTopCategories;
}
