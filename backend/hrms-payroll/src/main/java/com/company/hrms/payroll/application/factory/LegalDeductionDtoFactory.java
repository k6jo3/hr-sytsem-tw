package com.company.hrms.payroll.application.factory;

import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;

/**
 * 法扣款 DTO 工廠
 * 負責 Domain 物件與 Response DTO 之間的轉換
 */
public class LegalDeductionDtoFactory {

    private LegalDeductionDtoFactory() {
        // 工具類別禁止實例化
    }

    /**
     * 將 Domain 物件轉換為 Response DTO
     *
     * @param domain 法扣款聚合根
     * @return 法扣款回應 DTO
     */
    public static LegalDeductionResponse toResponse(LegalDeduction domain) {
        if (domain == null) {
            return null;
        }

        return LegalDeductionResponse.builder()
                .deductionId(domain.getId().getValue())
                .employeeId(domain.getEmployeeId())
                .courtOrderNumber(domain.getCourtOrderNumber())
                .garnishmentType(domain.getGarnishmentType().name())
                .totalAmount(domain.getTotalAmount())
                .deductedAmount(domain.getDeductedAmount())
                .remainingAmount(domain.getRemainingAmount())
                .priority(domain.getPriority())
                .effectiveDate(domain.getEffectiveDate())
                .expiryDate(domain.getExpiryDate())
                .status(domain.getStatus().name())
                .issuingAuthority(domain.getIssuingAuthority())
                .caseNumber(domain.getCaseNumber())
                .note(domain.getNote())
                .build();
    }
}
