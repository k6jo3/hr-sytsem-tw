package com.company.hrms.reporting.api.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LaborCostAnalysisResponse {
    private String organizationId;
    private String yearMonth;
    private BigDecimal totalCost;
    private Integer employeeCount;
    private BigDecimal averageCost;
}
