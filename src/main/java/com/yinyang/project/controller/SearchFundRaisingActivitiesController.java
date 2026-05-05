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
public class SearchFundRaisingActivitiesController {

    private FundRaisingActivity fundRaisingActivity;

    public PageBean<FundRaisingActivityResponse> searchFundRaisingActivities(@NotNull Integer pageNum, @NotNull Integer pageSize, String title, FundRaisingActivity.Status status, Integer categoryId, @NotBlank String order) {
        PageBean<FundRaisingActivityResponse> pb = new PageBean<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.FUND_RAISER) {
            fundRaisingActivity = new FundRaisingActivity();
            // start the pagination
            PageHelper.startPage(pageNum, pageSize);
            // get all fund raising activities
            List<FundRaisingActivityResponse> fundRaisingActivityResponseList = fundRaisingActivity.searchFundRaisingActivities(title, status, categoryId, order, currentUserId);
            // set the pagination result
            Page<FundRaisingActivityResponse> page = (Page<FundRaisingActivityResponse>) fundRaisingActivityResponseList;
            pb.setTotal(page.getTotal());
            pb.setItems(page.getResult());
        }
        return pb;
    }
}
