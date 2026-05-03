package com.yinyang.project.boundary;

import com.yinyang.project.controller.UpdateFRACategoryController;
import com.yinyang.project.entity.FRACategory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("fra/category/update")
public class UpdateFRACategoryPage {

    @Autowired
    private UpdateFRACategoryController updateFRACategoryController;

    @PutMapping
    public boolean updateFRACategory(@Valid @RequestBody FRACategory newFRACategoryData) {
        return updateFRACategoryController.updateFRACategory(newFRACategoryData);
    }
}
