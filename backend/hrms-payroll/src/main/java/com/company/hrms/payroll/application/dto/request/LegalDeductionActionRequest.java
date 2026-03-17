package com.company.hrms.payroll.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 法扣款動作請求 DTO
 * 用於暫停、恢復、終止等操作
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalDeductionActionRequest {

    /** 法扣款 ID（由 PathVariable 帶入） */
    private String deductionId;

    /** 備註原因（可選） */
    private String reason;
}
