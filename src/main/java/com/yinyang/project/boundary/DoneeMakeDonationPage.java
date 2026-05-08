package com.yinyang.project.boundary;

import com.yinyang.project.controller.DoneeMakeDonationController;
import com.yinyang.project.entity.Donation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/donee/donation/make")
public class DoneeMakeDonationPage {

    @Autowired
    private DoneeMakeDonationController doneeMakeDonationController;

    @PostMapping
    public boolean doneeMakeDonation(@Valid @RequestBody Donation donationData) {
        return doneeMakeDonationController.doneeMakeDonation(donationData);
    }

}
