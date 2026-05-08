package com.yinyang.project.dto;

import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.FundRaisingActivity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class DonationResponse {
    private Integer id;
    private Integer userAccountId;
    private Integer fraId;
    private BigDecimal amount;
    private Donation.Status status;
    private LocalDateTime createdAt;

    private String title;
    private Integer viewCount;
    private Integer shortlistCount;
    private FundRaisingActivity.Status fraStatus;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime endDate;
}
