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
 * 獎金查詢請求 (HR04-2.3)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetBonusListRequest extends PageRequest {

    /**
     * 員工編號
     */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /**
     * 獎金類型 (PERFORMANCE, YEAR_END, etc.)
     */
    @QueryFilter(property = "bonusType", operator = Operator.EQ)
    private String bonusType;

    /**
     * 發放年度
     */
    @QueryFilter(property = "payYear", operator = Operator.EQ)
    private Integer year;

    /**
     * 狀態 (APPROVED, PAID)
     */
    @QueryFilter(operator = Operator.EQ)
    private String status;
}
