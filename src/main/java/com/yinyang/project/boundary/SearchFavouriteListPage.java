package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchFavouriteListController;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.entity.FundRaisingActivity;
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
@RequestMapping("donee/favourite/search")
public class SearchFavouriteListPage {

    @Autowired
    private SearchFavouriteListController searchFavouriteListController;

    @GetMapping
    public List<FundRaisingActivityResponse> searchFavouriteList(@RequestParam(required = false) String title, @RequestParam(required = false) FundRaisingActivity.Status status, @RequestParam(required = false) Integer categoryId) {
        return searchFavouriteListController.searchFavouriteList(title, status, categoryId);
    }

}