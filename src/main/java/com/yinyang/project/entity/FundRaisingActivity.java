package com.yinyang.project.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FundRaisingActivity {
    private Integer id;
    private String title;
    private String description;
    private Integer viewCount;
    private Integer shortlistCount;
    private Status status;

    private Integer targetAmount;
    private Integer currentAmount;

    private Integer createdBy;
    private Integer categoryId;

    private LocalDateTime endDate;
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, SUSPENDED, COMPLETED
    }
}
