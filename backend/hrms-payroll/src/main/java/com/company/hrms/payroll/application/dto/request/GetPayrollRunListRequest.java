package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetPayrollRunListRequest extends PageRequest {
    @com.company.hrms.common.query.QueryCondition.EQ("organizationId")
    private String organizationId;

    @com.company.hrms.common.query.QueryCondition.GTE("periodStartDate")
    private LocalDate startDate;

    @com.company.hrms.common.query.QueryCondition.LTE("periodEndDate")
    private LocalDate endDate;

    @com.company.hrms.common.query.QueryCondition.EQ("status")
    private String status;
}
