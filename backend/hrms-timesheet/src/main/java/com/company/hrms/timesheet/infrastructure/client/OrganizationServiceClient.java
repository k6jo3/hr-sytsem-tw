package com.company.hrms.timesheet.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.hrms.timesheet.infrastructure.client.dto.OrganizationEmployeeListResponse;

@FeignClient(name = "hrms-organization", url = "${hrms.organization.url:http://localhost:8081}")
public interface OrganizationServiceClient {

    @GetMapping("/api/v1/employees")
    ResponseEntity<OrganizationEmployeeListResponse> getEmployeeList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "1000") int size);
}
