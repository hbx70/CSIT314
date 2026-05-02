package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ActivateUserProfileController {

    private UserProfile userProfile;

    public boolean activateUserProfile(String userProfileName) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userProfile = new UserProfile();
            return userProfile.activateUserProfile(userProfileName);
        }
        return false;
    }
}
