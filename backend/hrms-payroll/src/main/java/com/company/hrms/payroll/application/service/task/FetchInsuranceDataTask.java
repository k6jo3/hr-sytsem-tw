package com.company.hrms.payroll.application.service.task;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.infrastructure.client.insurance.InsuranceServiceClient;
import com.company.hrms.payroll.infrastructure.client.insurance.dto.CalculateFeeRequestDto;
import com.company.hrms.payroll.infrastructure.client.insurance.dto.FeeCalculationResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 獲取保險扣除任務
 * 從保險服務計算每位員工的保費扣除額
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchInsuranceDataTask implements PipelineTask<CalculatePayrollContext> {

    private final InsuranceServiceClient insuranceServiceClient;

    @Override
    public void execute(CalculatePayrollContext context) {
        log.info("正在從保險服務計算員工保費...");

        for (SalaryStructure struct : context.getEligibleStructures()) {
            if (struct.getMonthlySalary() == null || struct.getMonthlySalary().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            try {
                CalculateFeeRequestDto request = CalculateFeeRequestDto.builder()
                        .monthlySalary(struct.getMonthlySalary())
                        // 目前先預設不帶自提比例，或從薪資結構中獲取 (如果有的話)
                        .selfContributionRate(BigDecimal.ZERO)
                        .build();

                FeeCalculationResponseDto response = insuranceServiceClient.calculateFee(request);
                if (response != null) {
                    context.getInsuranceMap().put(struct.getEmployeeId(), response);
                }
            } catch (Exception e) {
                log.error("計算員工 {} 的保費失敗: {}", struct.getEmployeeId(), e.getMessage());
            }
        }

        log.info("成功獲取 {} 筆保費資訊", context.getInsuranceMap().size());
    }

    @Override
    public String getName() {
        return "FetchInsuranceDataTask";
    }
}
