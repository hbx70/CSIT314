package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchFundRaisingActivitiesController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.dto.PageBean;
import com.yinyang.project.entity.FundRaisingActivity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/search")
public class SearchFundRaisingActivitiesPage {

    @Autowired
    private SearchFundRaisingActivitiesController searchFundRaisingActivitiesController;

    @GetMapping
    public PageBean<FundRaisingActivityResponse> searchFundRaisingActivities(@NotNull Integer pageNum, @NotNull Integer pageSize, @RequestParam(required = false) String title, @RequestParam(required = false) FundRaisingActivity.Status status, @RequestParam(required = false) Integer categoryId, @NotBlank String order) {
        return searchFundRaisingActivitiesController.searchFundRaisingActivities(pageNum, pageSize, title, status, categoryId, order);
    }

}
