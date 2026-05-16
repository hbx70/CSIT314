package com.yinyang.project.controller;

import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DoneeSearchFundRaisingActivitiesController {

    private FundRaisingActivity fundRaisingActivity;

    public List<FundRaisingActivityResponse> doneeSearchFundRaisingActivities(String title, Integer categoryId, @NotBlank String orderBy) {
        List<FundRaisingActivityResponse> fundRaisingActivityResponseList = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.DONEE) {
            fundRaisingActivity = new FundRaisingActivity();
            fundRaisingActivityResponseList = fundRaisingActivity.doneeSearchFundRaisingActivities(title, categoryId, orderBy);
        }
        return fundRaisingActivityResponseList;
    }
}
