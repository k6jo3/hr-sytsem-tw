package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CheckInRequest {
    private String employeeId;
    private LocalDateTime checkInTime;
    private Double latitude;
    private Double longitude;
    private String ipAddress;
}
