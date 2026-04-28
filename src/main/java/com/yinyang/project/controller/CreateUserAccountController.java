package com.yinyang.project.controller;


import com.yinyang.project.entity.UserAccount;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserAccountController {

    private UserAccount userAccount;

    public boolean createUserAccount(UserAccount userAccountData) {
        userAccount = new UserAccount();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(userAccountData.getPassword());
        userAccountData.setPassword(encodedPassword);
        return userAccount.createUserAccount(userAccountData);
    }
}
