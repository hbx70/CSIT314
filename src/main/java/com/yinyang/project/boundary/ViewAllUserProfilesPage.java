package com.yinyang.project.boundary;


import com.yinyang.project.controller.ViewAllUserProfilesController;
import com.yinyang.project.entity.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Validated
@RequestMapping("/user/profile")
public class ViewAllUserProfilesPage {

    @Autowired
    private ViewAllUserProfilesController viewAllUserProfilesController;

    @GetMapping
    public List<UserProfile> getAllUserProfiles() {
        return viewAllUserProfilesController.getAllUserProfiles();
    }
}
