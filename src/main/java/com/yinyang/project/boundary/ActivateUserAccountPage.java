package com.yinyang.project.boundary;

import com.yinyang.project.controller.ActivateUserAccountController;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/account/activate")
public class ActivateUserAccountPage {

    @Autowired
    private ActivateUserAccountController activateUserAccountController;

    @PatchMapping
    public boolean activateUserAccount(@NotNull Integer userAccountId) {
        return activateUserAccountController.activateUserAccount(userAccountId);
    }

}