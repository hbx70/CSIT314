package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewFundRaisingActivityDetailsController;
import com.yinyang.project.controller.ViewFundRaisingActivityDetailsFromFavouriteController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("donee/favourite/detail")
public class ViewFundRaisingActivityDetailsFromFavouritePage {

    @Autowired
    private ViewFundRaisingActivityDetailsFromFavouriteController viewFundRaisingActivityDetailsFromFavouriteController;

    @GetMapping
    public FundRaisingActivityResponse viewFundRaisingActivityDetailsFromFavourite(@NotBlank Integer fundRaisingActivityId) {
        return viewFundRaisingActivityDetailsFromFavouriteController.viewFundRaisingActivityDetailsFromFavourite(fundRaisingActivityId);
    }
}
