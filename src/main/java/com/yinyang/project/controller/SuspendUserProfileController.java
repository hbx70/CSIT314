package com.yinyang.project.controller;

import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class SuspendUserProfileController {

    private UserProfile userProfile;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean suspendUserProfile(String userProfileName) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userProfile = new UserProfile();
            if (userProfile.suspendUserProfile(userProfileName)) {
                // Kick the user out of the system immediately
                String userProfileTokensKey = "user:tokens:profile:" + userProfileName;
                Set<String> tokens = stringRedisTemplate.opsForSet().members(userProfileTokensKey);
                if (tokens != null && !tokens.isEmpty()) {
                    for (String token : tokens) {
                        stringRedisTemplate.delete("token:" + token);
                    }
                    stringRedisTemplate.delete(userProfileTokensKey);
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
