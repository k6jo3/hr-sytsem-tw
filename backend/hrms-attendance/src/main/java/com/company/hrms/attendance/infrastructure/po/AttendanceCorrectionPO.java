package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance_corrections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCorrectionPO {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "attendance_record_id", length = 50)
    private String attendanceRecordId;

    @Column(name = "correction_date")
    private LocalDate correctionDate;

    @Column(name = "correction_type", length = 50)
    private String correctionType; // Enum string

    @Column(name = "corrected_check_in_time")
    private LocalTime correctedCheckInTime;

    @Column(name = "corrected_check_out_time")
    private LocalTime correctedCheckOutTime;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "rejection_reason", length = 500)
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
