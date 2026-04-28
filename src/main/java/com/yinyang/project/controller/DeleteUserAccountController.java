package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserAccountController {

    private UserAccount userAccount;

    public boolean deleteUserAccount(Integer userAccountId) {
        userAccount = new UserAccount();
        return userAccount.deleteUserAccount(userAccountId);
    }
}
