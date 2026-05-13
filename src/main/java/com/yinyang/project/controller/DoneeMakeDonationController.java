package com.yinyang.project.controller;

import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DoneeMakeDonationController {

    private Donation donation;

    private FundRaisingActivity fundRaisingActivity;

    public boolean doneeMakeDonation(@Valid Donation donationData) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        donationData.setUserAccountId(currentUserId);
        if (currentUserRole == UserProfile.Name.DONEE) {
            donation = new Donation();
            fundRaisingActivity = new FundRaisingActivity();
            FundRaisingActivity currFundRaisingActivity = fundRaisingActivity.getFundRaisingActivityById(donationData.getFraId());
            if (currFundRaisingActivity != null && currFundRaisingActivity.getStatus() == FundRaisingActivity.Status.ACTIVE) {
                BigDecimal targetAmount = currFundRaisingActivity.getTargetAmount();
                BigDecimal newAmount = currFundRaisingActivity.getCurrentAmount().add(donationData.getAmount());
                // target amount greater than or equals new amount
                if (targetAmount.compareTo(newAmount) >= 0) {
                    // record the donation
                    donation.doneeMakeDonation(donationData);
                    // update the fra current amount or status
                    if (donationData.getStatus() == Donation.Status.SUCCESS) {
                        fundRaisingActivity = new FundRaisingActivity();
                        fundRaisingActivity.doneeMakeDonation(newAmount, donationData.getFraId());
                        if (targetAmount.compareTo(newAmount) == 0) {
                            fundRaisingActivity.completeFundRaisingActivity(donationData.getFraId());
                        }
                        return true;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
