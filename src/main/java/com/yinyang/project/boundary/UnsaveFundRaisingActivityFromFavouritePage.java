package com.yinyang.project.boundary;

import com.yinyang.project.controller.UnsaveFundRaisingActivityFromFavouriteController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("donee/fra/unsave")
public class UnsaveFundRaisingActivityFromFavouritePage {

    @Autowired
    private UnsaveFundRaisingActivityFromFavouriteController unsaveFundRaisingActivityFromFavouriteController;

    @DeleteMapping
    public boolean unsaveFundRaisingActivityFromFavourite(@NotNull Integer fundRaisingActivityId) {
        return unsaveFundRaisingActivityFromFavouriteController.unsaveFundRaisingActivityFromFavourite(fundRaisingActivityId);
    }
}
