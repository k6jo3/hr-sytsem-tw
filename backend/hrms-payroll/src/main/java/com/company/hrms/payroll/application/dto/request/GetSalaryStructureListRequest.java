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
 * 薪資結構列表查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetSalaryStructureListRequest extends PageRequest {

    /**
     * 員工編號
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /**
     * 是否有效
     */
    @QueryFilter(property = "isActive", operator = Operator.EQ)
    private Boolean isActive;

    /**
     * 薪資制度 (MONTHLY, HOURLY)
     */
    @QueryFilter(property = "payrollSystem", operator = Operator.EQ)
    private String payrollSystem;

    /**
     * 特定生效日期
     */
    private LocalDate effectiveDate;
}
