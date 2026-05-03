package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchUserProfilesController {

    private UserProfile userProfile;

    public List<UserProfile> searchUserProfiles(UserProfile.Name name, String description, UserProfile.Status status, String order) {
        List<UserProfile> userProfiles = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userProfile = new UserProfile();
            userProfiles = userProfile.searchUserProfiles(name, description, status, order);
        }
        return userProfiles;
    }
}
