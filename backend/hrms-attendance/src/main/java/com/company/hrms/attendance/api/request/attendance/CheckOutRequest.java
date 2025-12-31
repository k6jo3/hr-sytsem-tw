package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CheckOutRequest {
    private String employeeId;
    private LocalDateTime checkOutTime;
    private Double latitude;
    private Double longitude;
    private String ipAddress;
}
