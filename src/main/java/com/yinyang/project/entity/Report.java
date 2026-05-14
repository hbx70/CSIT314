package com.yinyang.project.entity;

import com.yinyang.project.dto.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Report {
    private TrendDataResponse donationAmountTrend;
    private TrendDataResponse donationCountTrend;
    private TopDataResponse<TopData> donationTopCategories;
    private TrendDataResponse userAccountGrowthTrend;
    private MultiTrendDataResponse userAccountWithProfileGrowthTrend;
    private Range range;

    public enum Range {
        DAILY, WEEKLY, MONTHLY
    }
}
