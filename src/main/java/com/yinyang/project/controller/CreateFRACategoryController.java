package com.yinyang.project.controller;

import com.yinyang.project.entity.FRACategory;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CreateFRACategoryController {

    private FRACategory fraCategory;

    public boolean createFRACategory(FRACategory fraCategoryData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.PLATFORM_MANAGER) {
            fraCategory = new FRACategory();
            fraCategoryData.setName(fraCategory.getName().toUpperCase());
            fraCategoryData.setStatus(FRACategory.Status.ACTIVE);
            return fraCategory.createFRACategory(fraCategoryData);
        }
        return false;
    }
}
