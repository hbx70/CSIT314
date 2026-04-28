package com.yinyang.project.boundary;

import com.yinyang.project.controller.ActivateUserProfileController;
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
    public boolean activateUserProfile(Integer userProfileId) {
        return activateUserProfileController.activateUserProfile(userProfileId);
    }

}