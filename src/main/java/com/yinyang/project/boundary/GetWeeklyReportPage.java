package com.yinyang.project.boundary;

import com.yinyang.project.controller.GetMonthlyReportController;
import com.yinyang.project.controller.GetWeeklyReportController;
import com.yinyang.project.dto.Report;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/report/weekly")
public class GetWeeklyReportPage {

    @Autowired
    private GetWeeklyReportController getWeeklyReportController;

    @GetMapping
    public Report getWeeklyReport(@NotNull Integer size) {
        return getWeeklyReportController.getWeeklyReport(size);
    }

}
