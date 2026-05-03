package com.yinyang.project.boundary;

import com.yinyang.project.controller.SuspendUserProfileController;
import com.yinyang.project.entity.UserProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Validated
@RequestMapping("/user/profile/suspend")
public class SuspendUserProfilePage {
    @Autowired
    private SuspendUserProfileController suspendUserProfileController;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PatchMapping
    public boolean suspendUserProfile(@Valid @NotNull UserProfile.Name userProfileName) {
        if (suspendUserProfileController.suspendUserProfile(userProfileName)) {
            // Kick the user out of the system immediately
            String userProfileTokensKey = "user:tokens:profile:" + userProfileName.name();
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
}
