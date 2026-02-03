package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "overtime_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeApplicationPO {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "overtime_date")
    private LocalDate date;

    @Column(name = "hours")
    private Double hours;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "overtime_type", length = 50)
    private String overtimeType;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
