package com.company.hrms.insurance.application.service.fee;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.CalculateFeeRequest;
import com.company.hrms.insurance.api.response.FeeCalculationResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("calculateFeeServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CalculateFeeServiceImpl implements CommandApiService<CalculateFeeRequest, FeeCalculationResponse> {

    private final InsuranceLevelMatchingService levelMatchingService;
    private final InsuranceFeeCalculationService feeCalculationService;

    @Override
    public FeeCalculationResponse execCommand(CalculateFeeRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("保費計算請求: salary={}", request.getMonthlySalary());

        // 查詢適當的投保級距
        InsuranceLevel level = levelMatchingService
                .findAppropriateLevel(request.getMonthlySalary(), InsuranceType.LABOR, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("找不到適用的投保級距"));

        // 計算保費
        InsuranceFees fees = feeCalculationService.calculate(level, request.getSelfContributionRate());

        // 建構回應
        return FeeCalculationResponse.builder()
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
    }
}
