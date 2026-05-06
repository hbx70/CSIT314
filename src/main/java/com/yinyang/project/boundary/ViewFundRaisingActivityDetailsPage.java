package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewFundRaisingActivityDetailsController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("donee/fra/detail")
public class ViewFundRaisingActivityDetailsPage {

    @Autowired
    private ViewFundRaisingActivityDetailsController viewFundRaisingActivityDetailsController;

    @GetMapping
    public FundRaisingActivityResponse viewFundRaisingActivityDetails(@NotNull Integer fundRaisingActivityId) {
        return viewFundRaisingActivityDetailsController.viewFundRaisingActivityDetails(fundRaisingActivityId);
    }

}
