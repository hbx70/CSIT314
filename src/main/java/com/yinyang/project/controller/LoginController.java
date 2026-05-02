package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import org.springframework.stereotype.Service;


@Service
public class LoginController {

    private UserAccount userAccount;

    public String login(String username, String password) {
        userAccount = new UserAccount();
        return userAccount.login(username, password);
    }
}
