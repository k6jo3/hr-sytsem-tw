package com.company.hrms.reporting.api.request;

import lombok.Data;

@Data
public class GetLaborCostByDepartmentRequest {
    private String departmentId;
    private String yearMonth;
}
