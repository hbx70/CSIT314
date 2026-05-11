package com.yinyang.project.controller;

import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Map;

@Service
public class UpdateFundRaisingActivityController {

    private FundRaisingActivity fundRaisingActivity;

    public boolean updateFundRaisingActivity(FundRaisingActivity newFundRaisingActivityData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.FUND_RAISER && newFundRaisingActivityData.getId() != null) {
            System.out.println("FR + valid");
            fundRaisingActivity = new FundRaisingActivity();
            newFundRaisingActivityData.setTargetAmount(newFundRaisingActivityData.getTargetAmount().setScale(2, RoundingMode.HALF_UP));
            return fundRaisingActivity.updateFundRaisingActivity(newFundRaisingActivityData);
        }
        System.out.println("FR + not valid");
        return false;
    }
}
