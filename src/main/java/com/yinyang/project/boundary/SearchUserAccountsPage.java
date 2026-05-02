package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchUserAccountsController;
import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/user/account/search")
public class SearchUserAccountsPage {

    @Autowired
    private SearchUserAccountsController searchUserAccountsController;

    @GetMapping
    public List<UserAccount> searchUserAccounts(@RequestParam(required = false) String username, @RequestParam(required = false) String email, @RequestParam(required = false) String address, @RequestParam(required = false) UserProfile.Name userProfileName, @RequestParam(required = false) UserAccount.Status status) {
        return searchUserAccountsController.searchUserAccounts(username, email, address, userProfileName, status);
    }
}
