package com.company.hrms.reporting.api.response;

import lombok.Data;

@Data
public class UtilizationRateResponse {
    private String projectId;
    private String projectName;
    private String yearMonth;
    private Double utilizationRate;
    private Integer totalHours;
    private Integer billableHours;
}
