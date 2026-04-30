package com.yinyang.project.boundary;

import com.yinyang.project.controller.LogoutController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/logout")
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    @PostMapping
    public boolean logout(HttpServletRequest request){
        return logoutController.logout(request);
    }
}
