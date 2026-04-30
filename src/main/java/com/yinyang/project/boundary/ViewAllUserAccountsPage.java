package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewAllUserAccountsController;
import com.yinyang.project.controller.ViewAllUserProfilesController;
import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/user/account")
public class ViewAllUserAccountsPage {

    @Autowired
    private ViewAllUserAccountsController viewAllUserAccountsController;

    @GetMapping
    public List<UserAccount> getAllUserAccounts() {
        return viewAllUserAccountsController.getAllUserAccounts();
    }
}
