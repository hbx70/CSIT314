package com.yinyang.project.boundary;

import com.yinyang.project.controller.CreateFRACategoryController;
import com.yinyang.project.entity.FRACategory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("fra/category/create")
public class CreateFRACategoryPage {

    @Autowired
    private CreateFRACategoryController createFRACategoryController;

    @PostMapping
    public boolean createFRACategory(@Valid @RequestBody FRACategory FRACategoryData) {
        return createFRACategoryController.createFRACategory(FRACategoryData);
    }

}
