package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 薪資批次查詢請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetPayrollRunListRequest extends PageRequest {

    /**
     * 組織編號
     */
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    /**
     * 狀態
     */
    @QueryFilter(operator = Operator.EQ)
    private String status;

    /**
     * 開始日期 (查詢期間)
     */
    @QueryFilter(property = "periodStartDate", operator = Operator.GTE)
    private LocalDate startDate;

    /**
     * 結束日期 (查詢期間)
     */
    @QueryFilter(property = "periodEndDate", operator = Operator.LTE)
    private LocalDate endDate;

    /**
     * 是否排除已取消的批次
     */
    private Boolean excludeCancelled;
}
