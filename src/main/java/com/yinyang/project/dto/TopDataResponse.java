package com.yinyang.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopDataResponse <T>{
    private List<T> topDataList;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
