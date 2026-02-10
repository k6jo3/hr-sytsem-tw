package com.company.hrms.reporting.api.request;

import lombok.Data;

@Data
public class GetTurnoverAnalysisRequest {
    private String organizationId;
    private String yearMonth;
}
