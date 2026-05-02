package com.yinyang.project.boundary;

import com.yinyang.project.controller.SearchUserProfilesController;
import com.yinyang.project.entity.UserProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/user/profile/search")
public class SearchUserProfilesPage {

    @Autowired
    private SearchUserProfilesController searchUserProfilesController;

    @GetMapping
    public List<UserProfile> searchUserProfiles(@Valid @NotNull UserProfile.Name name, @NotBlank String description, @NotNull UserProfile.Status status) {
        return searchUserProfilesController.searchUserProfiles(name, description, status);
    }
}