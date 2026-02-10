package com.company.hrms.payroll.application.dto.request;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 加班費查詢請求 (HR04-2.5)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetOvertimePayListRequest extends PageRequest {

    /**
     * 員工編號
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /**
     * 部門編號
     */
    @QueryFilter(property = "deptId", operator = Operator.EQ)
    private String deptId;

    /**
     * 年月份 (格式: YYYY-MM)
     */
    @QueryFilter(property = "yearMonth", operator = Operator.EQ)
    private String yearMonth;
}
