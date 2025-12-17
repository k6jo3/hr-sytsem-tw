package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 員工合約持久化對象
 */
@Data
public class EmployeeContractPO {

    private UUID contractId;
    private UUID employeeId;
    private String contractType;
    private String contractNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal workingHours;
    private Integer trialPeriodMonths;
    private String attachmentUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
