package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class SuspendUserProfileController {

    private UserProfile userProfile;

    public boolean suspendUserProfile(Integer userProfileId) {
        userProfile = new UserProfile();
        return userProfile.suspendUserProfile(userProfileId);
    }
}
