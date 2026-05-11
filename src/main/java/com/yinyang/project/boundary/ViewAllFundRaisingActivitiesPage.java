package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewAllFundRaisingActivitiesController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.dto.PageBean;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@RequestMapping("/fra")
public class ViewAllFundRaisingActivitiesPage {

    @Autowired
    private ViewAllFundRaisingActivitiesController viewAllFundRaisingActivitiesController;

    @GetMapping
    public PageBean<FundRaisingActivityResponse> getAllFundRaisingActivities(@NotNull Integer pageNum, @NotNull Integer pageSize) {
        return viewAllFundRaisingActivitiesController.getAllFundRaisingActivities(pageNum, pageSize);
    }
}
