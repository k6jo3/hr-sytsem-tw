package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 員工合約持久化對象
 */
@Data
@Entity
@Table(name = "employee_contracts")
public class EmployeeContractPO {

    @Id
    private String id;
    private String employeeId;

    /** 合約編號 — 對應 MyBatis existsByContractNumber 查詢 */
    @Column(name = "contract_number")
    private String contractNumber;

    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer probationMonths;
    private Integer renewalCount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
