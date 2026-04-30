package com.yinyang.project.controller;


import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CreateUserAccountController {

    private UserAccount userAccount;

    public boolean createUserAccount(UserAccount userAccountData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userAccount = new UserAccount();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(userAccountData.getPassword());
            userAccountData.setPassword(encodedPassword);
            userAccountData.setStatus(UserAccount.Status.ACTIVE);
            return userAccount.createUserAccount(userAccountData);
        }
        return false;
    }
}
