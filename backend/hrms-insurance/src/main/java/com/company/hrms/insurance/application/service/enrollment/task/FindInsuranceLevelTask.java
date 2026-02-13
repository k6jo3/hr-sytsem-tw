package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢投保級距 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FindInsuranceLevelTask implements PipelineTask<EnrollmentContext> {

    private final InsuranceLevelMatchingService levelMatchingService;

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        var salary = context.getMonthlySalary();
        var date = context.getEnrollDate();

        log.debug("查詢投保級距: salary={}, date={}", salary, date);

        // 勞保級距
        InsuranceLevel laborLevel = levelMatchingService.findAppropriateLevel(salary, InsuranceType.LABOR, date)
                .orElseThrow(() -> new IllegalArgumentException("找不到適用的勞保級距，請確認薪資是否符合法規範圍"));
        context.setLaborLevel(laborLevel);

        // 健保級距 (通常與勞保相同)
        InsuranceLevel healthLevel = levelMatchingService.findAppropriateLevel(salary, InsuranceType.HEALTH, date)
                .orElse(laborLevel); // 若無健保級距，使用勞保級距
        context.setHealthLevel(healthLevel);

        // 勞退級距
        InsuranceLevel pensionLevel = levelMatchingService.findAppropriateLevel(salary, InsuranceType.PENSION, date)
                .orElse(laborLevel); // 若無勞退級距，使用勞保級距
        context.setPensionLevel(pensionLevel);

        log.info("投保級距查詢完成: 勞保第{}級({}), 健保第{}級({}), 勞退第{}級({})",
                laborLevel.getLevelNumber(), laborLevel.getMonthlySalary(),
                healthLevel.getLevelNumber(), healthLevel.getMonthlySalary(),
                pensionLevel.getLevelNumber(), pensionLevel.getMonthlySalary());
    }

    @Override
    public String getName() {
        return "查詢投保級距";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return context.getMonthlySalary() != null;
    }
}
