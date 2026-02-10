package com.company.hrms.reporting.api.request;

import lombok.Data;

@Data
public class GetUtilizationRateRequest {
    private String projectId;
    private String yearMonth;
}
