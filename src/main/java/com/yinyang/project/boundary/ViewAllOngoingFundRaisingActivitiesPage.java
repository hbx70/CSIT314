package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewAllOngoingFundRaisingActivitiesController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@RequestMapping("/fra")
public class ViewAllOngoingFundRaisingActivitiesPage {

    @Autowired
    private ViewAllOngoingFundRaisingActivitiesController viewAllOngoingFundRaisingActivitiesController;

    @GetMapping
    public List<FundRaisingActivityResponse> getAllOngoingFundRaisingActivities() {
        return viewAllOngoingFundRaisingActivitiesController.getAllOngoingFundRaisingActivities();
    }
}
