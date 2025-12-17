package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 員工人事歷程持久化對象
 */
@Data
public class EmployeeHistoryPO {

    private UUID historyId;
    private UUID employeeId;
    private String eventType;
    private LocalDate effectiveDate;
    private String oldValue;  // JSON
    private String newValue;  // JSON
    private String reason;
    private UUID approvedBy;
    private LocalDateTime createdAt;
}
