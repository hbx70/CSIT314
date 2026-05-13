package com.yinyang.project.boundary;

import com.yinyang.project.controller.ViewReportController;
import com.yinyang.project.entity.Report;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/report")
public class ViewReportPage {

    @Autowired
    private ViewReportController viewReportController;

    @GetMapping
    public Report getReport(@NotNull Integer size, @NotNull Report.Range range) {
        return viewReportController.getReport(size, range);
    }

}
