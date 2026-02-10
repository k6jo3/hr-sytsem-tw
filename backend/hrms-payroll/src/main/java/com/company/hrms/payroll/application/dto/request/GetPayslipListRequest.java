package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 薪資單查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetPayslipListRequest extends PageRequest {

    /**
     * 薪資批次編號
     */
    @QueryFilter(property = "runId", operator = Operator.EQ)
    private String runId;

    /**
     * 員工編號
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /**
     * 狀態
     */
    @QueryFilter(operator = Operator.EQ)
    private String status;

    /**
     * 年月份 (格式: YYYY-MM)
     */
    private String yearMonth;

    /**
     * 發放日期
     */
    @QueryFilter(operator = Operator.EQ)
    private LocalDate payDate;
}
