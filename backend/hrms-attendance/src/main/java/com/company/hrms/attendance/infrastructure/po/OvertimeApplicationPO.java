package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OvertimeApplicationPO {
    private String id;
    private String employeeId;
    private LocalDate date;
    private Double hours;
    private String status;
    private String reason;
    private String overtimeType;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
