package com.company.hrms.insurance.application.service.withdrawal.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 執行退保 Task
 */
@Component
@Slf4j
public class PerformWithdrawalTask implements PipelineTask<WithdrawalContext> {

    @Override
    public void execute(WithdrawalContext context) throws Exception {
        var enrollment = context.getEnrollment();
        var withdrawDate = context.getWithdrawDate();

        log.debug("執行退保: enrollmentId={}, date={}",
                enrollment.getId().getValue(), withdrawDate);

        // 執行退保 (Domain 方法)
        enrollment.withdraw(withdrawDate);

        log.info("退保執行成功: status={}", enrollment.getStatus());
    }

    @Override
    public String getName() {
        return "執行退保";
    }
}
