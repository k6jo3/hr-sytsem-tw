package com.company.hrms.reporting.api.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LaborCostByDepartmentResponse {
    private String departmentId;
    private String departmentName;
    private String yearMonth;
    private BigDecimal totalCost;
    private Integer employeeCount;
}
