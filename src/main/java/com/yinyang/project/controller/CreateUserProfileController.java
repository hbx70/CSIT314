package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import org.springframework.stereotype.Service;


@Service
public class CreateUserProfileController {

    private UserProfile userProfile;

    public boolean createUserProfile(UserProfile userProfileData) {
        userProfile = new UserProfile();
        return userProfile.createUserProfile(userProfileData);
    }
}
