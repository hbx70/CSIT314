package com.yinyang.project.boundary;

import com.yinyang.project.controller.SuspendFundRaisingActivityController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/suspend")
public class SuspendFundRaisingActivityPage {

    @Autowired
    private SuspendFundRaisingActivityController suspendFundRaisingActivityController;

    @PatchMapping
    public boolean suspendFundRaisingActivity(@NotNull Integer fundRaisingActivityId) {
        return  suspendFundRaisingActivityController.suspendFundRaisingActivity(fundRaisingActivityId);
    }

}
