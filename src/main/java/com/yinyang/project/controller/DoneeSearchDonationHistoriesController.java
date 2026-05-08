package com.yinyang.project.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import com.yinyang.project.dto.PageBean;
import com.yinyang.project.entity.Donation;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DoneeSearchDonationHistoriesController {

    private Donation donation;

    public PageBean<DonationResponse> searchDonationHistories(@NotNull Integer pageNum, @NotNull Integer pageSize, Donation.Status status, String title, @NotBlank String orderBy) {
        PageBean<DonationResponse> pb = new PageBean<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.DONEE) {
            donation = new Donation();
            // start the pagination
            PageHelper.startPage(pageNum, pageSize);
            // get all fund raising activities
            List<DonationResponse> donationResponseList = donation.searchDonationHistories(status, title, orderBy, currentUserId);
            // set the pagination result
            Page<DonationResponse> page = (Page<DonationResponse>) donationResponseList;
            pb.setTotal(page.getTotal());
            pb.setItems(page.getResult());
        }
        return pb;
    }
}
