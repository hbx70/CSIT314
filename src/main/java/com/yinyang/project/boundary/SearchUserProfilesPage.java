package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchUserProfilesController;
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
@RequestMapping("/user/profile/search")
public class SearchUserProfilesPage {

    @Autowired
    private SearchUserProfilesController searchUserProfilesController;

    @GetMapping
    public List<UserProfile> searchUserProfiles(@RequestParam(required = false) UserProfile.Name name, @RequestParam(required = false) String description, @RequestParam(required = false) UserProfile.Status status) {
        return searchUserProfilesController.searchUserProfiles(name, description, status);
    }
}