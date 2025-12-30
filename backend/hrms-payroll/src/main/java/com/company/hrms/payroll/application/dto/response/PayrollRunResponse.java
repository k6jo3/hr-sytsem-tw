package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRunResponse {
    private String runId;
    private String name;
    private String status;
    private String payrollSystem;
    private LocalDate start;
    private LocalDate end;
    private int totalDays;

    // Statistics
    private int totalEmployees;
    private int processedEmployees;
    private int successCount;
    private int failureCount;
    private BigDecimal totalGrossPay;
    private BigDecimal totalNetPay;
    private BigDecimal totalDeductions;

    // Timestamps
    private LocalDateTime executedAt;
    private LocalDateTime completedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
}
