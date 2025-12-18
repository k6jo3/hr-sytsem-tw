package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 員工合約持久化對象
 */
@Data
public class EmployeeContractPO {

    private String id;
    private String employeeId;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer probationMonths;
    private Integer renewalCount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
