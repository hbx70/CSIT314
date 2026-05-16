package com.yinyang.project.controller;

import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DoneeSearchHistoryDonationsController {

    private Donation donation;

    public List<DonationResponse> searchHistoryDonations(Donation.Status status, String title, @NotBlank String orderBy) {
        List<DonationResponse> donationResponseList = new ArrayList<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.DONEE) {
            donation = new Donation();
            donationResponseList = donation.searchHistoryDonations(status, title, orderBy, currentUserId);
        }
        return donationResponseList;
    }
}
