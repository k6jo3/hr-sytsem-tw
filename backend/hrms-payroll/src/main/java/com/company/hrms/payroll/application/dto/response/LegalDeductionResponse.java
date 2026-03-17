package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 法扣款回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalDeductionResponse {

    /** 法扣款 ID */
    private String deductionId;

    /** 員工 ID */
    private String employeeId;

    /** 法院執行命令字號 */
    private String courtOrderNumber;

    /** 扣款類型 */
    private String garnishmentType;

    /** 總扣款金額 */
    private BigDecimal totalAmount;

    /** 已扣款金額 */
    private BigDecimal deductedAmount;

    /** 剩餘應扣金額 */
    private BigDecimal remainingAmount;

    /** 優先順序 */
    private Integer priority;

    /** 生效日 */
    private LocalDate effectiveDate;

    /** 到期日 */
    private LocalDate expiryDate;

    /** 狀態 */
    private String status;

    /** 發布機關 */
    private String issuingAuthority;

    /** 案件編號 */
    private String caseNumber;

    /** 備註 */
    private String note;

    /** 建立時間 */
    private LocalDateTime createdAt;

    /** 更新時間 */
    private LocalDateTime updatedAt;
}
