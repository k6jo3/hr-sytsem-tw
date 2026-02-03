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
@Table(name = "leave_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationPO {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "leave_type_id", length = 50, nullable = false)
    private String leaveTypeId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "start_period", length = 20)
    private String startPeriod;

    @Column(name = "end_period", length = 20)
    private String endPeriod;

    @Column(name = "proof_attachment_url", length = 255)
    private String proofAttachmentUrl;

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
