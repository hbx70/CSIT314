package com.yinyang.project.boundary;

import com.yinyang.project.controller.UpdateFundRaisingActivityController;
import com.yinyang.project.entity.FundRaisingActivity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("fra/update")
public class UpdateFundRaisingActivityPage {

    @Autowired
    private UpdateFundRaisingActivityController updateFundRaisingActivityController;

    @PutMapping
    public boolean updateFundRaisingActivity(@Valid @RequestBody FundRaisingActivity newFundRaisingActivityData) {
        return updateFundRaisingActivityController.updateFundRaisingActivity(newFundRaisingActivityData);
    }

}
