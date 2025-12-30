package com.company.hrms.attendance.infrastructure.po;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LeaveBalancePO {
    private String id;
    private String employeeId;
    private String leaveTypeId;
    private Integer year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
