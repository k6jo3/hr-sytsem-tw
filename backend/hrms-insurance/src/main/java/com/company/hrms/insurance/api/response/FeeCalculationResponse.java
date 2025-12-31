package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保費計算結果回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationResponse {

    /** 投保級距 */
    private int levelNumber;

    /** 投保薪資 */
    private BigDecimal monthlySalary;

    /** 勞保費 - 員工負擔 */
    private BigDecimal laborEmployeeFee;

    /** 勞保費 - 雇主負擔 */
    private BigDecimal laborEmployerFee;

    /** 健保費 - 員工負擔 */
    private BigDecimal healthEmployeeFee;

    /** 健保費 - 雇主負擔 */
    private BigDecimal healthEmployerFee;

    /** 勞退 - 雇主提繳 */
    private BigDecimal pensionEmployerFee;

    /** 勞退 - 個人自提 */
    private BigDecimal pensionSelfContribution;

    /** 員工每月負擔總計 */
    private BigDecimal totalEmployeeFee;

    /** 雇主每月負擔總計 */
    private BigDecimal totalEmployerFee;
}
