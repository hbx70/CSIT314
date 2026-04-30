package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchUserAccountController {

    private UserAccount userAccount;

    public List<UserAccount> searchUserAccount(String username, String email, String address, Integer userProfileId, UserAccount.Status status) {
        List<UserAccount> userAccounts = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.ADMIN) {
            userAccount = new UserAccount();
            userAccounts = userAccount.searchUserAccount(username, email, address, userProfileId, status);
            return userAccounts;
        }
        return userAccounts;
    }
}