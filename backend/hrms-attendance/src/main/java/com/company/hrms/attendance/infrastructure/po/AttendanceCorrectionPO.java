package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttendanceCorrectionPO {
    private String id;
    private String employeeId;
    private String attendanceRecordId;
    private String correctionType; // Enum string
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
