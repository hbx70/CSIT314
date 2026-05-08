package com.yinyang.project.boundary;

import com.yinyang.project.controller.DoneeSearchDonationHistoriesController;
import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.dto.PageBean;
import com.yinyang.project.entity.Donation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/donee/donation/search")
public class DoneeSearchDonationHistoriesPage {

    @Autowired
    private DoneeSearchDonationHistoriesController doneeSearchDonationHistoriesController;

    @GetMapping
    public PageBean<DonationResponse> searchDonationHistories(@NotNull Integer pageNum, @NotNull Integer pageSize, @RequestParam(required = false) Donation.Status status, @RequestParam(required = false) String title, @NotBlank String orderBy) {
        return doneeSearchDonationHistoriesController.searchDonationHistories(pageNum, pageSize, status, title, orderBy);
    }

}
