package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetPayrollRunListRequest extends PageRequest {
    private String organizationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
