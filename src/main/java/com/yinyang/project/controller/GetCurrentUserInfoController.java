package com.yinyang.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetCurrentUserInfoController {

    private UserAccount userAccount;

    public UserAccount getCurrentUserInfo() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims != null) {
            userAccount = new UserAccount();
            Integer currUserId = (Integer) claims.get("id");
            UserAccount currentUserInfo = userAccount.getUserAccountById(currUserId);
            currentUserInfo.setPassword(null);
            return currentUserInfo;
        }
        return null;
    }
}
