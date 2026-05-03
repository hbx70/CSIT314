package com.yinyang.project.controller;

import com.yinyang.project.entity.FRACategory;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SuspendFRACategoryController {

    private FRACategory fraCategory;

    public boolean suspendFRACategory(Integer fraCategoryId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.PLATFORM_MANAGER) {
            fraCategory = new FRACategory();
            return fraCategory.suspendFRACategory(fraCategoryId);
        }
        return false;
    }
}
