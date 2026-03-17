package com.company.hrms.payroll.application.factory;

import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;

/**
 * 預借薪資 DTO 工廠
 * 負責 Domain → Response DTO 的轉換
 */
public class SalaryAdvanceDtoFactory {

    private SalaryAdvanceDtoFactory() {
        // 工具類不允許實例化
    }

    /**
     * 將 Domain 物件轉換為 Response DTO
     *
     * @param domain 預借薪資聚合根
     * @return SalaryAdvanceResponse
     */
    public static SalaryAdvanceResponse toResponse(SalaryAdvance domain) {
        if (domain == null) {
            return null;
        }

        return SalaryAdvanceResponse.builder()
                .advanceId(domain.getId().getValue())
                .employeeId(domain.getEmployeeId())
                .requestedAmount(domain.getRequestedAmount())
                .approvedAmount(domain.getApprovedAmount())
                .installmentMonths(domain.getInstallmentMonths())
                .installmentAmount(domain.getInstallmentAmount())
                .repaidAmount(domain.getRepaidAmount())
                .remainingBalance(domain.getRemainingBalance())
                .applicationDate(domain.getApplicationDate())
                .disbursementDate(domain.getDisbursementDate())
                .status(domain.getStatus().name())
                .reason(domain.getReason())
                .rejectionReason(domain.getRejectionReason())
                .approverId(domain.getApproverId())
                .build();
    }
}
