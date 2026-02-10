package com.company.hrms.reporting.api.response;

import lombok.Data;

@Data
public class TurnoverAnalysisResponse {
    private String organizationId;
    private String yearMonth;
    private Double turnoverRate;
    private Integer totalEmployees;
    private Integer newHires;
    private Integer terminations;
}
