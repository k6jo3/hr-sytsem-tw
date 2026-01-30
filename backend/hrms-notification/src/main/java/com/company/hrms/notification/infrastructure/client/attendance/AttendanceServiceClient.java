package com.company.hrms.notification.infrastructure.client.attendance;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.hrms.notification.infrastructure.client.attendance.dto.AnnualLeaveExpiryDto;

@FeignClient(name = "hrms-attendance", url = "${hrms.attendance.url:http://localhost:8081}")
public interface AttendanceServiceClient {

    @GetMapping("/api/v1/leaves/annual/expiring")
    List<AnnualLeaveExpiryDto> getExpiringAnnualLeaves(@RequestParam("expiryDate") String expiryDate);
}
