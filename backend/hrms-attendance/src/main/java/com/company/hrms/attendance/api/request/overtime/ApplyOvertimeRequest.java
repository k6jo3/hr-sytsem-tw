package com.company.hrms.attendance.api.request.overtime;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ApplyOvertimeRequest {
    private String employeeId;
    private LocalDate date;
    private Double hours;
    private String overtimeType; // WEEKDAY, WEEKEND, HOLIDAY
    private String reason;
}
