package com.yinyang.project.boundary;

import com.yinyang.project.controller.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/login")
public class LoginPage {

    @Autowired
    private LoginController loginController;

    @PostMapping
    public String login(String username, String password) {
        return loginController.login(username, password);
    }
}
