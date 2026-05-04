package com.yinyang.project.boundary;

import com.yinyang.project.controller.CreateFundRaisingActivityController;
import com.yinyang.project.entity.FundRaisingActivity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/fra/create")
public class CreateFundRaisingActivityPage {

    @Autowired
    private CreateFundRaisingActivityController createFundRaisingActivityController;

    @PostMapping
    public boolean createFundRaisingActivity(@Valid @RequestBody FundRaisingActivity fundRaisingActivityData) {
        return createFundRaisingActivityController.createFundRaisingActivity(fundRaisingActivityData);
    }

}
