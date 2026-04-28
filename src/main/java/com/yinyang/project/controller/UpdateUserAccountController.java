package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserAccountController {

    private UserAccount userAccount;

    public boolean updateUserAccount(UserAccount newUserAccountData) {
        userAccount = new UserAccount();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newUserAccountData.getPassword());
        newUserAccountData.setPassword(encodedPassword);
        return userAccount.updateUserAccount(newUserAccountData);
    }
}
