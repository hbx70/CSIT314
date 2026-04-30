package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class SuspendUserAccountController {

    private UserAccount userAccount;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean suspendUserAccount(Integer userAccountId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userAccount = new UserAccount();
            if (userAccount.suspendUserAccount(userAccountId)) {
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
        return false;
    }
}
