package com.yinyang.project.boundary;

import com.yinyang.project.controller.SuspendUserAccountController;
import com.yinyang.project.controller.SuspendUserProfileController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/profile/suspend")
public class SuspendUserProfilePage {
    @Autowired
    private SuspendUserProfileController suspendUserProfileController;

    @PatchMapping
    public boolean suspendUserProfile(@Valid @NotNull String userProfileName) {
        return suspendUserProfileController.suspendUserProfile(userProfileName);
    }
}
