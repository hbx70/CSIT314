package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewNumberOfFRAShortlistsController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/shortlists")
public class ViewNumberOfFRAShortlistsPage {

    @Autowired
    private ViewNumberOfFRAShortlistsController viewNumberOfFRAShortlistsController;

    @GetMapping
    public Integer getNumberOfFRAShortlists(@NotNull Integer fundRaisingActivityId) {
        return viewNumberOfFRAShortlistsController.getNumberOfFRAShortlists(fundRaisingActivityId);
    }

}
