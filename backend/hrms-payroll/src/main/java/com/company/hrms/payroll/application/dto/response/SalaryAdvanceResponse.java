package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 預借薪資回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryAdvanceResponse {

    /** 預借 ID */
    private String advanceId;

    /** 員工 ID */
    private String employeeId;

    /** 申請金額 */
    private BigDecimal requestedAmount;

    /** 核准金額 */
    private BigDecimal approvedAmount;

    /** 分期月數 */
    private Integer installmentMonths;

    /** 每期扣回金額 */
    private BigDecimal installmentAmount;

    /** 已扣回金額 */
    private BigDecimal repaidAmount;

    /** 剩餘未扣回金額 */
    private BigDecimal remainingBalance;

    /** 申請日期 */
    private LocalDate applicationDate;

    /** 撥款日期 */
    private LocalDate disbursementDate;

    /** 狀態 */
    private String status;

    /** 申請原因 */
    private String reason;

    /** 駁回原因 */
    private String rejectionReason;

    /** 核准人 ID */
    private String approverId;

    /** 建立時間 */
    private LocalDateTime createdAt;

    /** 更新時間 */
    private LocalDateTime updatedAt;
}
