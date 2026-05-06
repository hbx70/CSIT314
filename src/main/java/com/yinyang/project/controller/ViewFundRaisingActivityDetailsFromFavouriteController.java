package com.yinyang.project.controller;

import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.entity.FavouriteFRA;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ViewFundRaisingActivityDetailsFromFavouriteController {

    private FundRaisingActivity fundRaisingActivity;

    public FundRaisingActivityResponse viewFundRaisingActivityDetailsFromFavourite(@NotBlank Integer fundRaisingActivityId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.DONEE) {
            fundRaisingActivity = new FundRaisingActivity();
            return fundRaisingActivity.viewFundRaisingActivityDetails(fundRaisingActivityId);
        }
        return null;
    }
}
