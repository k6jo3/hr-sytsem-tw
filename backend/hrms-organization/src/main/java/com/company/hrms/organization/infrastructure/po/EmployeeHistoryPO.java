package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 員工人事歷程持久化對象
 */
@Data
@Entity
@Table(name = "employee_history")
public class EmployeeHistoryPO {

    @Id
    @Column(name = "history_id")
    private String id;
    private String employeeId;
    private String eventType;
    private LocalDate eventDate;
    private String description;
    private String oldValue;
    private String newValue;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
}
