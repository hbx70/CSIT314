package com.yinyang.project.boundary;

import com.yinyang.project.controller.CreateUserAccountController;
import com.yinyang.project.entity.UserAccount;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/account/create")
public class CreateUserAccountPage {

    @Autowired
    private CreateUserAccountController createUserAccountController;

    @PostMapping
    public boolean createUserAccount(@Valid @RequestBody UserAccount userAccountData) {
        return createUserAccountController.createUserAccount(userAccountData);
    }
}
