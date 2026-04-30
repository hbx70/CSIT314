package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchUserAccountController;
import com.yinyang.project.entity.UserAccount;
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
public class SearchUserAccountPage {

    @Autowired
    private SearchUserAccountController searchUserAccountController;

    @GetMapping
    public List<UserAccount> searchUserAccount(@RequestParam(required = false) String username, @RequestParam(required = false) String email, @RequestParam(required = false) String address, @RequestParam(required = false) Integer userProfileId, @RequestParam(required = false) UserAccount.Status status) {
        return searchUserAccountController.searchUserAccount(username, email, address, userProfileId, status);
    }
}
