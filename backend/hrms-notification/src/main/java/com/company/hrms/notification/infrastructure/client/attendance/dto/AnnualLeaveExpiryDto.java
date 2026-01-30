package com.company.hrms.notification.infrastructure.client.attendance.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AnnualLeaveExpiryDto {
    private String employeeId;
    private Double remainingDays;
    private LocalDate expiryDate;
}
