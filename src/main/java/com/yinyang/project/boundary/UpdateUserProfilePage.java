package com.yinyang.project.boundary;

import com.yinyang.project.controller.UpdateUserProfileController;
import com.yinyang.project.entity.UserProfile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: validate user role

@RestController
@Validated
@RequestMapping("/user/profile/update")
public class UpdateUserProfilePage {

    @Autowired
    private UpdateUserProfileController updateUserProfileController;

    @PutMapping
    public boolean updateUserProfile(@Valid @RequestBody UserProfile newUserProfile) {
        return updateUserProfileController.updateUserProfile(newUserProfile);
    }
}
