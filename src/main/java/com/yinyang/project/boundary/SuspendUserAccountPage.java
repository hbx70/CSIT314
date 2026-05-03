package com.yinyang.project.boundary;

import com.yinyang.project.controller.SuspendUserAccountController;
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
@RequestMapping("/user/account/suspend")
public class SuspendUserAccountPage {

    @Autowired
    private SuspendUserAccountController suspendUserAccountController;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PatchMapping
    public boolean suspendUserAccount(@Valid @NotNull Integer userAccountId) {
        if (suspendUserAccountController.suspendUserAccount(userAccountId)) {
            // Kick the user out of the system immediately
            String userTokensKey = "user:tokens:id:" + userAccountId;
            Set<String> tokens = stringRedisTemplate.opsForSet().members(userTokensKey);
            if (tokens != null && !tokens.isEmpty()) {
                for (String token : tokens) {
                    stringRedisTemplate.delete("token:" + token);
                }
                stringRedisTemplate.delete(userTokensKey);
            }
            return true;
        }
        return false;
    }
}
