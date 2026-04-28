package com.yinyang.project.boundary;

import com.yinyang.project.controller.DeleteUserAccountController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/profile/delete")
public class DeleteUserAccountPage {

    @Autowired
    private DeleteUserAccountController deleteUserAccountController;

    @DeleteMapping
    public boolean deleteUserAccount(@Valid @NotNull Integer userAccountId) {
        return deleteUserAccountController.deleteUserAccount(userAccountId);
    }
}
