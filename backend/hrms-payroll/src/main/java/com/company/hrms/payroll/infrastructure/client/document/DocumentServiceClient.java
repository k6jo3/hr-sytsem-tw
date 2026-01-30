package com.company.hrms.payroll.infrastructure.client.document;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.company.hrms.payroll.infrastructure.client.document.dto.DocumentDto;

@FeignClient(name = "hrms-document", url = "${hrms.document.url:http://localhost:8080}")
public interface DocumentServiceClient {

    @GetMapping("/api/v1/documents/{id}")
    DocumentDto getDocument(@PathVariable("id") String id);
}
