package com.company.hrms.payroll.application.dto.request;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetPayslipListRequest extends PageRequest {

    @QueryFilter(property = "payrollRunId", operator = Operator.EQ)
    private String runId;

    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;
}
