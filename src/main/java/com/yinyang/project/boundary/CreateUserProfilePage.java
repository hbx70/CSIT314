package com.yinyang.project.boundary;

import com.yinyang.project.controller.CreateUserProfileController;
import com.yinyang.project.entity.UserProfile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: validate user role

@RestController
@Validated
@RequestMapping("/user/profile/create")
public class CreateUserProfilePage {

    @Autowired
    private CreateUserProfileController createUserProfileController;

    @PostMapping
    public boolean createUserProfile(@Valid @RequestBody UserProfile userProfileData) {
        return createUserProfileController.createUserProfile(userProfileData);
    }

}
