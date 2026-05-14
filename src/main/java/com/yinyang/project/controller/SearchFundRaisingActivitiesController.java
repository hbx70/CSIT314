package com.yinyang.project.controller;

import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchFundRaisingActivitiesController {

    private FundRaisingActivity fundRaisingActivity;

    public List<FundRaisingActivityResponse> searchFundRaisingActivities(String title, FundRaisingActivity.Status status, Integer categoryId, @NotBlank String order) {
        List<FundRaisingActivityResponse> fundRaisingActivityResponseList = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.FUND_RAISER) {
            fundRaisingActivity = new FundRaisingActivity();
            fundRaisingActivityResponseList = fundRaisingActivity.searchFundRaisingActivities(title, status, categoryId, order, currentUserId);
        }
        return fundRaisingActivityResponseList;
    }
}
