package com.yinyang.project.boundary;

import com.yinyang.project.controller.SuspendUserAccountController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@RequestMapping("/user/account/suspend")
public class SuspendUserAccountPage {

    @Autowired
    private SuspendUserAccountController suspendUserAccountController;

    @PatchMapping
    public boolean suspendUserAccount(@Valid @NotNull Integer userAccountId) {
        return suspendUserAccountController.suspendUserAccount(userAccountId);
    }
}
