package com.company.hrms.recruitment.infrastructure.client.organization;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.company.hrms.recruitment.infrastructure.client.organization.dto.DepartmentDto;

@FeignClient(name = "hrms-organization", url = "${hrms.organization.url:http://localhost:8080}")
public interface OrganizationServiceClient {

    @GetMapping("/api/v1/departments/{id}")
    DepartmentDto getDepartment(@PathVariable("id") String id);
}
