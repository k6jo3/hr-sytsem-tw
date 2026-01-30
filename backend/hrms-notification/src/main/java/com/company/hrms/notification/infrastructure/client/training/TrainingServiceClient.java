package com.company.hrms.notification.infrastructure.client.training;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.hrms.notification.infrastructure.client.training.dto.CertificateExpiryDto;

@FeignClient(name = "hrms-training", url = "${hrms.training.url:http://localhost:8082}")
public interface TrainingServiceClient {

    @GetMapping("/api/v1/certificates/expiring")
    List<CertificateExpiryDto> getExpiringCertificates(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);
}
