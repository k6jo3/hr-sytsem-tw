package com.company.hrms.payroll.application.factory;

import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;

public class PayrollRunDtoFactory {

    public static PayrollRunResponse toResponse(PayrollRun domain) {
        if (domain == null) {
            return null;
        }

        return PayrollRunResponse.builder()
                .runId(domain.getId().getValue())
                .name(domain.getName())
                .status(domain.getStatus().name())
                .payrollSystem(domain.getPayrollSystem().name())
                .start(domain.getPayPeriod().getStartDate())
                .end(domain.getPayPeriod().getEndDate())
                .totalDays((int) domain.getPayPeriod().getDays())
                .totalEmployees(domain.getStatistics().getTotalEmployees())
                .processedEmployees(domain.getStatistics().getProcessedEmployees())
                .successCount(domain.getStatistics().getProcessedEmployees())
                .failureCount(domain.getStatistics().getFailedEmployees())
                .totalGrossPay(domain.getStatistics().getTotalGrossAmount())
                .totalNetPay(domain.getStatistics().getTotalNetAmount())
                .totalDeductions(domain.getStatistics().getTotalDeductions())
                .executedAt(domain.getExecutedAt())
                .completedAt(domain.getCompletedAt())
                .approvedAt(domain.getApprovedAt())
                .paidAt(domain.getPaidAt())
                .build();
    }
}
