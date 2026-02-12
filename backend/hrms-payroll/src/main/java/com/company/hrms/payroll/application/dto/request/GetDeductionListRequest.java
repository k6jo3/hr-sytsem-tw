package com.company.hrms.payroll.application.dto.request;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 扣款項目查詢請求 (HR04-2.4)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetDeductionListRequest extends PageRequest {

    /**
     * 員工編號
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /**
     * 扣款類型 (LOAN, etc.)
     */
    @QueryFilter(property = "deductionType", operator = Operator.EQ)
    private String deductionType;

    /**
     * 狀態 (ACTIVE, COMPLETED)
     */
    @QueryFilter(operator = Operator.EQ)
    private String status;
}
