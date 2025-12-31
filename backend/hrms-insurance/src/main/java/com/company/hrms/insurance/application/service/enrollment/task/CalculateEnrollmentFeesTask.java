package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 計算保費 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CalculateEnrollmentFeesTask implements PipelineTask<EnrollmentContext> {

    private final InsuranceFeeCalculationService feeCalculationService;

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        var laborLevel = context.getLaborLevel();
        var selfContributionRate = context.getRequest().getSelfContributionRate();

        log.debug("計算保費: 投保薪資={}, 自提比例={}",
                laborLevel.getMonthlySalary(), selfContributionRate);

        InsuranceFees fees = feeCalculationService.calculate(laborLevel, selfContributionRate);
        context.setFees(fees);

        log.info("保費計算完成: 員工負擔={}, 雇主負擔={}",
                fees.getTotalEmployeeFee(), fees.getTotalEmployerFee());
    }

    @Override
    public String getName() {
        return "計算保費";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return context.getLaborLevel() != null;
    }
}
