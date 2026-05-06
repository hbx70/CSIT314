package com.yinyang.project.boundary;

import com.yinyang.project.controller.SaveFundRaisingActivityToFavouriteController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("donee/fra/save")
public class SaveFundRaisingActivityToFavouritePage {

    @Autowired
    private SaveFundRaisingActivityToFavouriteController saveFundRaisingActivityToFavouriteController;

    @PostMapping
    public boolean saveFundRaisingActivityToFavourite(@NotNull Integer fundRaisingActivityId) {
        return saveFundRaisingActivityToFavouriteController.saveFundRaisingActivityToFavourite(fundRaisingActivityId);
    }

}
