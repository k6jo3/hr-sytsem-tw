package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ShiftPO {
    private String id;
    private String name;
    private String type;
    private String startTime; // HH:mm:ss
    private String endTime; // HH:mm:ss
    private String breakStartTime; // HH:mm:ss
    private String breakEndTime; // HH:mm:ss
    private Integer lateToleranceMinutes;
    private Integer earlyLeaveToleranceMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
