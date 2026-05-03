package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchUserAccountsController {

    private UserAccount userAccount;

    public List<UserAccount> searchUserAccounts(String username, String email, String address, UserProfile.Name userProfileName, UserAccount.Status status, String order) {
        List<UserAccount> userAccounts = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userAccount = new UserAccount();
            userAccounts = userAccount.searchUserAccounts(username, email, address, userProfileName, status, order);
        }
        return userAccounts;
    }
}