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
 * 法扣款列表查詢請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetLegalDeductionListRequest extends PageRequest {

    /** 員工 ID 篩選 */
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    /** 狀態篩選 */
    @QueryFilter(property = "status", operator = Operator.EQ)
    private String status;

    /** 扣款類型篩選 */
    @QueryFilter(property = "garnishmentType", operator = Operator.EQ)
    private String garnishmentType;
}
