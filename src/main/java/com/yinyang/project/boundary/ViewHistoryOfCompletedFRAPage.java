package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewHistoryOfCompletedFRAController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/fra/completed")
public class ViewHistoryOfCompletedFRAPage {

    @Autowired
    private ViewHistoryOfCompletedFRAController viewHistoryOfCompletedFRAController;

    @GetMapping
    public List<FundRaisingActivityResponse> viewHistoryOfCompletedFRA() {
        return viewHistoryOfCompletedFRAController.getHistoryOfCompletedFRA();
    }

}
