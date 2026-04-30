package com.yinyang.project.boundary;

import com.yinyang.project.controller.UpdateUserAccountController;
import com.yinyang.project.entity.UserAccount;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/account/update")
public class UpdateUserAccountPage {

    @Autowired
    private UpdateUserAccountController updateUserAccountController;

    @PutMapping
    public boolean updateUserAccount(@Valid @RequestBody UserAccount newUserAccountData) {
        return updateUserAccountController.updateUserAccount(newUserAccountData);
    }
}
