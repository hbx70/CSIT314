package com.yinyang.project.boundary;

import com.yinyang.project.controller.DoneeViewDetailsOfHistoryDonationController;
import com.yinyang.project.dto.DonationResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/donee/donation/detail")
public class DoneeViewDetailsOfHistoryDonationPage {

    @Autowired
    private DoneeViewDetailsOfHistoryDonationController doneeViewDetailsOfHistoryDonationController;

    @GetMapping
    public DonationResponse getDonationDetails(@NotNull Integer donationId) {
        return doneeViewDetailsOfHistoryDonationController.getDonationDetails(donationId);
    }
}
