package com.yinyang.project.controller;

import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ViewNumberOfFRAShortlistsController {

    private FundRaisingActivity fundRaisingActivity;

    public Integer getNumberOfFRAShortlists(@NotNull Integer fundRaisingActivityId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.FUND_RAISER) {
            fundRaisingActivity = new FundRaisingActivity();
            return fundRaisingActivity.getNumberOfFRAShortlists(fundRaisingActivityId);
        }
        return null;
    }
}
