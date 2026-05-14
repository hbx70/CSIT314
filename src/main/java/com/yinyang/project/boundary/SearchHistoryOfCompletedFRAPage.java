package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchHistoryOfCompletedFRAController;
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
@RequestMapping("/fra/search/completed")
public class SearchHistoryOfCompletedFRAPage {

    @Autowired
    private SearchHistoryOfCompletedFRAController searchHistoryOfCompletedFRAController;

    @GetMapping
    public List<FundRaisingActivityResponse> searchHistoryOfCompletedFRA(@RequestParam(required = false) String title, @RequestParam(required = false) Integer categoryId, @NotBlank String order) {
        return searchHistoryOfCompletedFRAController.searchHistoryOfCompletedFRA(title, categoryId, order);
    }
}
