package com.yinyang.project.dto;

import com.yinyang.project.entity.FRACategory;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundRaisingActivityResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer viewCount;
    private Integer shortlistCount;
    private FundRaisingActivity.Status status;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    private String creatorName;
    private UserProfile.Name creatorRole;
    private UserAccount.Status creatorAccountStatus;

    private Integer categoryId;
    private String categoryName;
    private FRACategory.Status categoryStatus;

    private LocalDateTime createdAt;
}
