package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewNumberOfFRAViewsController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/views")
public class ViewNumberOfFRAViewsPage {

    @Autowired
    private ViewNumberOfFRAViewsController viewNumberOfFRAViewsController;

    @GetMapping
    public Integer getNumberOfFRAViews(@NotNull Integer fundRaisingActivityId) {
        return viewNumberOfFRAViewsController.getNumberOfFRAViews(fundRaisingActivityId);
    }

}
