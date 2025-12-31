package com.company.hrms.insurance.application.service.adjustment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢新投保級距 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FindNewLevelTask implements PipelineTask<AdjustmentContext> {

    private final InsuranceLevelMatchingService levelMatchingService;

    @Override
    public void execute(AdjustmentContext context) throws Exception {
        var enrollment = context.getEnrollment();
        var newSalary = context.getNewMonthlySalary();
        var effectiveDate = context.getEffectiveDate();

        log.debug("查詢新投保級距: type={}, newSalary={}",
                enrollment.getInsuranceType(), newSalary);

        InsuranceLevel newLevel = levelMatchingService
                .findAppropriateLevel(newSalary, enrollment.getInsuranceType(), effectiveDate)
                .orElseThrow(() -> new IllegalStateException(
                        "找不到適用的投保級距: salary=" + newSalary));

        context.setNewLevel(newLevel);
        log.info("新投保級距: level={}, salary={}",
                newLevel.getLevelNumber(), newLevel.getMonthlySalary());
    }

    @Override
    public String getName() {
        return "查詢新投保級距";
    }
}
