package com.yinyang.project.boundary;

import com.yinyang.project.controller.ActivateFRACategoryController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("fra/category/activate")
public class ActivateFRACategoryPage {

    @Autowired
    private ActivateFRACategoryController activateFRACategoryController;

    @PatchMapping
    public boolean activateFRACategory(@NotNull Integer FRACategoryId) {
        return activateFRACategoryController.activateFRACategory(FRACategoryId);
    }

}
