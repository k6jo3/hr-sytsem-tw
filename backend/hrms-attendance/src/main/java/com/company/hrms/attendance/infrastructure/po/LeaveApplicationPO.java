package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LeaveApplicationPO {
    private String id;
    private String employeeId;
    private String leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;
    private String startPeriod;
    private String endPeriod;
    private String proofAttachmentUrl;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
