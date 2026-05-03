package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchFRACategoryController;
import com.yinyang.project.entity.FRACategory;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/fra/category/search")
public class SearchFRACategoryPage {

    @Autowired
    private SearchFRACategoryController searchFRACategoryController;

    @GetMapping
    public List<FRACategory> searchFRACategory(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) FRACategory.Status status, @NotBlank String order) {
        return searchFRACategoryController.searchFRACategory(name, description, status, order);
    }

}
