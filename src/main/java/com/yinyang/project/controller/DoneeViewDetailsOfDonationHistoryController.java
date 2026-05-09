package com.yinyang.project.controller;

import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DoneeViewDetailsOfDonationHistoryController {

    private Donation donation;

    public DonationResponse getDonationDetails(@NotNull Integer donationId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        if (currentUserRole == UserProfile.Name.DONEE) {
            donation = new Donation();
            return donation.getDonationDetailsById(donationId);
        }
        return null;
    }
}
