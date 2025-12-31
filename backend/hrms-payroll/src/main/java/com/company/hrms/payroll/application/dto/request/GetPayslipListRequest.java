package com.company.hrms.payroll.application.dto.request;

import com.company.hrms.common.api.request.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetPayslipListRequest extends PageRequest {
    private String runId;
    private String employeeId;
}
