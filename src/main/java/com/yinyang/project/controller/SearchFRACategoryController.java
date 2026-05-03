package com.yinyang.project.controller;

import com.yinyang.project.entity.FRACategory;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchFRACategoryController {

    private FRACategory fraCategory;

    public List<FRACategory> searchFRACategory(String name, String description, FRACategory.Status status, @NotNull String order) {
        List<FRACategory> fraCategoryList = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.PLATFORM_MANAGER) {
            fraCategory = new FRACategory();
            fraCategoryList = fraCategory.searchFRACategory(name.toUpperCase(), description, status, order);
        }
        return fraCategoryList;
    }
}
