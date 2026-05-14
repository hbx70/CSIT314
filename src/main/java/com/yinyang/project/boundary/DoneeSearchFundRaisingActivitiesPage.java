package com.yinyang.project.boundary;

import com.yinyang.project.controller.DoneeSearchFundRaisingActivitiesController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/donee/fra/search")
public class DoneeSearchFundRaisingActivitiesPage {

    @Autowired
    private DoneeSearchFundRaisingActivitiesController doneeSearchFundRaisingActivitiesController;

    @GetMapping
    public List<FundRaisingActivityResponse> doneeSearchFundRaisingActivities(@RequestParam(required = false) String title, @RequestParam(required = false) Integer categoryId, @NotBlank String orderBy) {
        return doneeSearchFundRaisingActivitiesController.doneeSearchFundRaisingActivities(title, categoryId, orderBy);
    }

}
