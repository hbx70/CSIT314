package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserProfileController {

    private UserProfile userProfile;

    public boolean updateUserProfile(UserProfile newUserProfile) {
        userProfile = new UserProfile();
        return userProfile.updateUserProfile(newUserProfile);
    }
}
