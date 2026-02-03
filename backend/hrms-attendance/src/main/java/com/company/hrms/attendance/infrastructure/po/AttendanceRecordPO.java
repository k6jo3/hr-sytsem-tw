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
@Table(name = "attendance_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordPO {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "record_date")
    private LocalDate date;

    @Column(name = "shift_id", length = 50)
    private String shiftId;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "is_late")
    private Boolean isLate;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "is_early_leave")
    private Boolean isEarlyLeave;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes;

    @Column(name = "anomaly_type", length = 50)
    private String anomalyType;

    @Column(name = "is_corrected")
    private Boolean isCorrected;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
