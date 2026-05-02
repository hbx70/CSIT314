package com.yinyang.project.boundary;

import com.yinyang.project.controller.GetCurrentUserInfoController;
import com.yinyang.project.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/info")
public class GetCurrentUserInfoPage {

    @Autowired
    private GetCurrentUserInfoController getCurrentUserInfoController;

    @GetMapping
    public UserAccount getCurrentUserInfo() {
        return getCurrentUserInfoController.getCurrentUserInfo();
    }
}
