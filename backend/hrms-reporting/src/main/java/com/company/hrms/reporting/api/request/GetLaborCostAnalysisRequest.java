package com.company.hrms.reporting.api.request;

import lombok.Data;

@Data
public class GetLaborCostAnalysisRequest {
    private String organizationId;
    private String yearMonth;
}
