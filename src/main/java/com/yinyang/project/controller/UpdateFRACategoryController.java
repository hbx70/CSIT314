package com.yinyang.project.controller;

import com.yinyang.project.entity.FRACategory;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UpdateFRACategoryController {

    private FRACategory fraCategory;

    public boolean updateFRACategory(FRACategory newFRACategoryData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.PLATFORM_MANAGER && newFRACategoryData.getId() != null) {
            fraCategory = new FRACategory();
            newFRACategoryData.setName(newFRACategoryData.getName().toUpperCase());
            return fraCategory.updateFRACategory(newFRACategoryData);
        }
        return false;
    }
}
