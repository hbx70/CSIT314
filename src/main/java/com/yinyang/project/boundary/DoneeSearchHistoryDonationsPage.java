package com.yinyang.project.boundary;

import com.yinyang.project.controller.DoneeSearchHistoryDonationsController;
import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.entity.Donation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/donee/donation/search")
public class DoneeSearchHistoryDonationsPage {

    @Autowired
    private DoneeSearchHistoryDonationsController doneeSearchHistoryDonationsController;

    @GetMapping
    public List<DonationResponse> searchHistoryDonations(@RequestParam(required = false) Donation.Status status, @RequestParam(required = false) String title, @NotBlank String orderBy) {
        return doneeSearchHistoryDonationsController.searchHistoryDonations(status, title, orderBy);
    }

}
