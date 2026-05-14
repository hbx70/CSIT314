package com.yinyang.project.controller;

import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.Report;
import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ViewReportController {

    private Donation donation;

    private UserAccount userAccount;

    public Report getReport(@NotNull Integer size, @NotNull Report.Range range) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUerRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUerRole == UserProfile.Name.PLATFORM_MANAGER) {
            Report report = new Report();
            donation = new Donation();
            userAccount = new UserAccount();
            report.setDonationAmountTrend(donation.getDonationAmountTrendReport(size, range));
            report.setDonationCountTrend(donation.getDonationCountTrendReport(size, range));
            report.setDonationTopCategories(donation.getDonationTopCategoriesReport(size, range));
            report.setUserAccountGrowthTrend(userAccount.getUserAccountGrowthTrendReport(size, range));
            report.setUserAccountWithProfileGrowthTrend(userAccount.getUserAccountWithProfileGrowthTrendReport(size, range));
            return report;
        }
        return null;
    }
}
