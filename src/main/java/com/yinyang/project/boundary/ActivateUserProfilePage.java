package com.yinyang.project.boundary;

import com.yinyang.project.controller.ActivateUserProfileController;
import com.yinyang.project.entity.UserProfile;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/profile/activate")
public class ActivateUserProfilePage {

    @Autowired
    private ActivateUserProfileController activateUserProfileController;

    @PatchMapping
    public boolean activateUserProfile(@NotNull UserProfile.Name userProfileName) {
        return activateUserProfileController.activateUserProfile(userProfileName);
    }
}
