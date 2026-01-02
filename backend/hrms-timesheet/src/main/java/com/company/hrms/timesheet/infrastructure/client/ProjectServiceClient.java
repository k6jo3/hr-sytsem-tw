package com.company.hrms.timesheet.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto;

@FeignClient(name = "hrms-project", url = "${hrms.project.url:http://localhost:8080}") // Configure URL properly
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects/{id}")
    ResponseEntity<ProjectDto> getProjectDetail(@PathVariable("id") String id);
}
