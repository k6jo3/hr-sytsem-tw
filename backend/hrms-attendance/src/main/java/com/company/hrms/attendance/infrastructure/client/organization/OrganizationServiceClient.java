package com.company.hrms.attendance.infrastructure.client.organization;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeListResponseDto;

@FeignClient(name = "hrms-organization", url = "${hrms.organization.url:http://localhost:8080}")
public interface OrganizationServiceClient {

    @GetMapping("/api/v1/employees")
    EmployeeListResponseDto getEmployeeList(
            @RequestParam(value = "status", required = false) List<String> statuses,
            @RequestParam(value = "organizationId", required = false) String organizationId,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "1000") Integer size);

    @GetMapping("/api/v1/employees/{employeeId}")
    com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeDetailDto getEmployeeDetail(
            @org.springframework.web.bind.annotation.PathVariable("employeeId") String employeeId);
}
