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
 * 預借薪資列表查詢請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetSalaryAdvanceListRequest extends PageRequest {

    /** 員工 ID */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /** 狀態 */
    @QueryFilter(property = "status", operator = Operator.EQ)
    private String status;
}
