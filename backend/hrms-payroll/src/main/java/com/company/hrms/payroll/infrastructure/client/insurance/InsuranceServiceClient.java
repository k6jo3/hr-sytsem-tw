package com.company.hrms.payroll.infrastructure.client.insurance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.company.hrms.payroll.infrastructure.client.insurance.dto.CalculateFeeRequestDto;
import com.company.hrms.payroll.infrastructure.client.insurance.dto.FeeCalculationResponseDto;

@FeignClient(name = "hrms-insurance", url = "${hrms.insurance.url:http://localhost:8080}")
public interface InsuranceServiceClient {

    @PostMapping("/api/v1/insurance/fees/calculate")
    FeeCalculationResponseDto calculateFee(@RequestBody CalculateFeeRequestDto request);
}
