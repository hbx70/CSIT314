package com.yinyang.project.controller;

import com.yinyang.project.entity.Report;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ViewReportController {

    private Report report;

    public Report getReport(@NotNull Integer size, @NotNull Report.Range range) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUerRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUerRole == UserProfile.Name.PLATFORM_MANAGER) {
            report = new Report();
            return report.getReport(size, range);
        }
        return null;
    }
}
