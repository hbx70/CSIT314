package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UpdateUserAccountController {

    private UserAccount userAccount;

    public boolean updateUserAccount(UserAccount newUserAccountData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if  (currentUserRole == UserProfile.Name.ADMIN && newUserAccountData.getId() != null) {
            userAccount = new UserAccount();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(newUserAccountData.getPassword());
            newUserAccountData.setPassword(encodedPassword);
            return userAccount.updateUserAccount(newUserAccountData);
        }
        return false;
    }
}
