package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewAllUserProfilesController {

    private UserProfile userProfile;

    public List<UserProfile> getAllUserProfiles(String name, String email, String status, String role) {
        userProfile = new UserProfile();
        return userProfile.getAllUserProfiles(name, email, status, role);
    }
}
