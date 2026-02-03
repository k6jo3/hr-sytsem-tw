package com.company.hrms.workflow.infrastructure.client.organization;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.company.hrms.common.api.response.ApiResponse;
import com.company.hrms.workflow.infrastructure.client.organization.dto.EmployeeDto;

@FeignClient(name = "hrms-organization", url = "${hrms.organization.url:http://localhost:8080}")
public interface OrganizationServiceClient {

    @GetMapping("/api/v1/employees/{id}")
    ApiResponse<EmployeeDto> getEmployeeDetail(@PathVariable("id") String id);
}
