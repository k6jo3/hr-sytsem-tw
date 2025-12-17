package com.company.hrms.organization.api.request.employee;

import lombok.Data;

/**
 * 員工查詢請求 DTO
 */
@Data
public class EmployeeQueryRequest {

    private String search;
    private String status;
    private String departmentId;
    private String organizationId;
    private String hireDateFrom;
    private String hireDateTo;
    private Integer page = 1;
    private Integer size = 20;
}
