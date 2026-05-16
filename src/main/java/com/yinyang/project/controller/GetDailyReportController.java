package com.yinyang.project.controller;

import com.yinyang.project.dto.Report;
import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetDailyReportController {

    private Donation donation;

    public Report getDailyReport(@NotNull Integer size) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.PLATFORM_MANAGER) {
            donation = new Donation();
            return donation.getDailyReport(size);
        }
        return null;
    }
}
