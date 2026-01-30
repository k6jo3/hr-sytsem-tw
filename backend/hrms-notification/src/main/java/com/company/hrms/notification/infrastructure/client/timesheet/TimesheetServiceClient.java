package com.company.hrms.notification.infrastructure.client.timesheet;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hrms-timesheet", url = "${hrms.timesheet.url:http://localhost:8083}")
public interface TimesheetServiceClient {

    @GetMapping("/api/v1/timesheets/missing")
    List<String> getEmployeesWithoutTimesheet(@RequestParam("date") String date);
}
