package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class ActivateUserProfileController {
    private UserProfile userProfile;

    public boolean activateUserProfile(Integer userProfileId) {
        userProfile = new UserProfile();
        return userProfile.activateUserProfile(userProfileId);
    }
}
