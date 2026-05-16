package com.yinyang.project.boundary;

import com.yinyang.project.controller.GetMonthlyReportController;
import com.yinyang.project.dto.Report;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/report/monthly")
public class GetMonthlyReportPage {

    @Autowired
    private GetMonthlyReportController getMonthlyReportController;

    @GetMapping
    public Report getMonthlyReport(@NotNull Integer size) {
        return getMonthlyReportController.getMonthlyReport(size);
    }

}
