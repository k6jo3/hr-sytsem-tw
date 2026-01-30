package com.company.hrms.notification.infrastructure.client.organization;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeListResponseDto;

@FeignClient(name = "hrms-organization", url = "${hrms.organization.url:http://localhost:8080}")
public interface OrganizationServiceClient {

    @GetMapping("/api/v1/employees")
    EmployeeListResponseDto getEmployeeList(
            @RequestParam(value = "status", required = false) java.util.List<String> statuses,
            @RequestParam(value = "departmentId", required = false) java.util.List<String> departmentIds,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "1000") Integer size);

    @GetMapping("/api/v1/employees/{id}")
    EmployeeDto getEmployeeDetail(@PathVariable("id") String id);

    @GetMapping("/api/v1/employees/birthday")
    java.util.List<EmployeeDto> getEmployeesByBirthday(
            @RequestParam("month") int month,
            @RequestParam("day") int day);

    @GetMapping("/api/v1/employees/contracts/expiring")
    java.util.List<com.company.hrms.notification.infrastructure.client.organization.dto.ContractDto> getExpiringContracts(
            @RequestParam("expiryDate") String expiryDate);
}
