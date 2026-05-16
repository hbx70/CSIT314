package com.yinyang.project.boundary;

import com.yinyang.project.controller.GetDailyReportController;
import com.yinyang.project.dto.Report;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/report/daily")
public class GetDailyReportPage {

    @Autowired
    private GetDailyReportController getDailyReportController;

    @GetMapping
    public Report getDailyReport(@NotNull Integer size) {
        return getDailyReportController.getDailyReport(size);
    }

}
