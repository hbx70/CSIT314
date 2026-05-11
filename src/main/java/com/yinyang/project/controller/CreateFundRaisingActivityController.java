package com.yinyang.project.controller;

import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CreateFundRaisingActivityController {

    private FundRaisingActivity fundRaisingActivity;

    public boolean createFundRaisingActivity(FundRaisingActivity fundRaisingActivityData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.FUND_RAISER) {
            fundRaisingActivity = new FundRaisingActivity();
            fundRaisingActivityData.setViewCount(0);
            fundRaisingActivityData.setShortlistCount(0);
            fundRaisingActivityData.setStatus(FundRaisingActivity.Status.ACTIVE);
            fundRaisingActivityData.setTargetAmount(fundRaisingActivityData.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
            fundRaisingActivityData.setCurrentAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            fundRaisingActivityData.setCreatedBy(currentUserId);
            return fundRaisingActivity.createFundRaisingActivity(fundRaisingActivityData);
        }
        return false;
    }
}
