package com.company.hrms.insurance.api.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.insurance.api.request.CalculateFeeRequest;
import com.company.hrms.insurance.api.request.CalculateSupplementaryPremiumRequest;
import com.company.hrms.insurance.api.response.FeeCalculationResponse;
import com.company.hrms.insurance.api.response.SupplementaryPremiumResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.aggregate.SupplementaryPremium;
import com.company.hrms.insurance.domain.model.valueobject.IncomeType;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 費用計算 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-Fee", description = "費用計算")
public class HR05FeeCmdController {

    private final InsuranceFeeCalculationService feeCalculationService;
    private final InsuranceLevelMatchingService levelMatchingService;

    @PostMapping("/fees/calculate")
    @Operation(summary = "計算保費", operationId = "calculateFee")
    public ResponseEntity<FeeCalculationResponse> calculateFee(
            @RequestBody CalculateFeeRequest request) {

        log.info("保費計算請求: salary={}", request.getMonthlySalary());

        // 查詢適當的投保級距
        InsuranceLevel level = levelMatchingService
                .findAppropriateLevel(request.getMonthlySalary(), InsuranceType.LABOR, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("找不到適用的投保級距"));

        // 計算保費
        InsuranceFees fees = feeCalculationService.calculate(level, request.getSelfContributionRate());

        // 建構回應
        FeeCalculationResponse response = FeeCalculationResponse.builder()
                .levelNumber(level.getLevelNumber())
                .monthlySalary(level.getMonthlySalary())
                .laborEmployeeFee(fees.getLaborEmployeeFee())
                .laborEmployerFee(fees.getLaborEmployerFee())
                .healthEmployeeFee(fees.getHealthEmployeeFee())
                .healthEmployerFee(fees.getHealthEmployerFee())
                .pensionEmployerFee(fees.getPensionEmployerFee())
                .pensionSelfContribution(fees.getPensionSelfContribution())
                .totalEmployeeFee(fees.getTotalEmployeeFee())
                .totalEmployerFee(fees.getTotalEmployerFee())
                .build();

        log.info("保費計算完成: level={}, employeeFee={}, employerFee={}",
                level.getLevelNumber(), fees.getTotalEmployeeFee(), fees.getTotalEmployerFee());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/supplementary-premium/calculate")
    @Operation(summary = "計算補充保費", operationId = "calculateSupplementaryPremium")
    public ResponseEntity<SupplementaryPremiumResponse> calculateSupplementaryPremium(
            @RequestBody CalculateSupplementaryPremiumRequest request) {

        log.info("補充保費計算請求: employeeId={}, income={}",
                request.getEmployeeId(), request.getIncomeAmount());

        // 解析收入類型
        IncomeType incomeType = IncomeType.valueOf(request.getIncomeType());
        LocalDate incomeDate = LocalDate.parse(request.getIncomeDate());

        // 計算補充保費
        SupplementaryPremium premium = SupplementaryPremium.calculate(
                request.getEmployeeId(),
                incomeType,
                incomeDate,
                request.getIncomeAmount(),
                request.getInsuredSalary());

        // 建構回應
        SupplementaryPremiumResponse response;
        if (premium == null) {
            // 不需繳納補充保費
            response = SupplementaryPremiumResponse.builder()
                    .required(false)
                    .threshold(request.getInsuredSalary().multiply(new java.math.BigDecimal("4")))
                    .incomeAmount(request.getIncomeAmount())
                    .insuredSalary(request.getInsuredSalary())
                    .build();
        } else {
            response = SupplementaryPremiumResponse.builder()
                    .required(true)
                    .threshold(premium.getThreshold())
                    .premiumBase(premium.getPremiumBase())
                    .premiumAmount(premium.getPremiumAmount())
                    .incomeAmount(request.getIncomeAmount())
                    .insuredSalary(request.getInsuredSalary())
                    .build();
        }

        log.info("補充保費計算完成: required={}, amount={}",
                response.isRequired(), response.getPremiumAmount());

        return ResponseEntity.ok(response);
    }
}
