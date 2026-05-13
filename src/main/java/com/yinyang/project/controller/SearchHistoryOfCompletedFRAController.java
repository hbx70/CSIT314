package com.yinyang.project.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.dto.PageBean;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchHistoryOfCompletedFRAController {

    private FundRaisingActivity fundRaisingActivity;

    public PageBean<FundRaisingActivityResponse> searchHistoryOfCompletedFRA(@NotNull Integer pageNum, @NotNull Integer pageSize, String title, Integer categoryId, @NotBlank String order) {
        PageBean<FundRaisingActivityResponse> pb = new PageBean<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.FUND_RAISER) {
            fundRaisingActivity = new FundRaisingActivity();
            Integer offset = (pageNum - 1) * pageSize;
            pb = fundRaisingActivity.searchFundRaisingActivities(title, FundRaisingActivity.Status.COMPLETED, categoryId, order, currentUserId, pageSize, offset);
        }
        return pb;
    }
}
