package com.yinyang.project.boundary;

import com.yinyang.project.controller.ActivateFundRaisingActivityController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/activate")
public class ActivateFundRaisingActivityPage {

    @Autowired
    private ActivateFundRaisingActivityController activateFundRaisingActivityController;

    @PatchMapping
    public boolean activateFundRaisingActivity(@NotNull Integer fundRaisingActivityId) {
        return activateFundRaisingActivityController.activateFundRaisingActivity(fundRaisingActivityId);
    }
}
