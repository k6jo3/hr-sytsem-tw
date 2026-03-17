package com.company.hrms.payroll.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立法扣款請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLegalDeductionRequest {

    /** 員工 ID */
    @NotBlank(message = "員工 ID 不可為空")
    private String employeeId;

    /** 法院執行命令字號 */
    @NotBlank(message = "法院執行命令字號不可為空")
    private String courtOrderNumber;

    /** 扣款類型（COURT_ORDER / ADMINISTRATIVE_LEVY） */
    @NotBlank(message = "扣款類型不可為空")
    private String garnishmentType;

    /** 總扣款金額 */
    @NotNull(message = "總扣款金額不可為空")
    @Positive(message = "總扣款金額必須大於 0")
    private BigDecimal totalAmount;

    /** 優先順序（預設 1） */
    @Builder.Default
    private Integer priority = 1;

    /** 生效日 */
    @NotNull(message = "生效日不可為空")
    private LocalDate effectiveDate;

    /** 到期日（可選） */
    private LocalDate expiryDate;

    /** 發布機關（可選） */
    private String issuingAuthority;

    /** 案件編號（可選） */
    private String caseNumber;

    /** 備註（可選） */
    private String note;
}
