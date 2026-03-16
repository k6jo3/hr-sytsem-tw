package com.company.hrms.payroll.infrastructure.client.attendance;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

/**
 * Attendance Service (考勤服務) 外部客戶端
 */
@FeignClient(name = "hrms-attendance", url = "${hrms.attendance-service.url:http://localhost:8083/api/v1/attendance}")
public interface AttendanceServiceClient {

    /**
     * 取得員工月度考勤彙總
     */
    @GetMapping("/reports/monthly")
    MonthlyReportResponse getMonthlyReport(
            @RequestParam("organizationId") String organizationId,
            @RequestParam("year") int year,
            @RequestParam("month") int month);

    @Data
    class MonthlyReportResponse {
        private List<MonthlyReportItem> items;
    }

    @Data
    class MonthlyReportItem {
        private String employeeId;
        private BigDecimal totalWorkingHours;      // 總工時
        private BigDecimal workdayOvertimeHours;
        private BigDecimal restDayOvertimeHours;
        private BigDecimal holidayOvertimeHours;
        private BigDecimal leaveDays;
        private BigDecimal unpaidLeaveHours;       // 事假時數
        private BigDecimal sickLeaveHours;         // 病假時數
    }
}
