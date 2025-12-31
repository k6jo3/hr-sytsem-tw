package com.company.hrms.attendance.api.request.leave;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ApplyLeaveRequest {
    private String employeeId;
    private String leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startPeriod; // FULL_DAY, MORNING, AFTERNOON
    private String endPeriod;
    private String reason;
    private String proofAttachmentUrl;
}
