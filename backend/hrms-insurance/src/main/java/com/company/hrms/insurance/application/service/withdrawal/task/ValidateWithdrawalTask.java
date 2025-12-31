package com.company.hrms.insurance.application.service.withdrawal.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * 驗證退保條件 Task
 */
@Component
@Slf4j
public class ValidateWithdrawalTask implements PipelineTask<WithdrawalContext> {

    @Override
    public void execute(WithdrawalContext context) throws Exception {
        var enrollment = context.getEnrollment();
        var withdrawDate = context.getWithdrawDate();

        log.debug("驗證退保條件: enrollmentId={}, status={}",
                enrollment.getId().getValue(), enrollment.getStatus());

        // 檢查狀態
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("只有已加保狀態可以退保，目前狀態: " + enrollment.getStatus());
        }

        // 檢查退保日期
        if (withdrawDate.isBefore(enrollment.getEnrollDate())) {
            throw new IllegalArgumentException("退保日期不可早於加保日期");
        }

        log.info("退保驗證通過");
    }

    @Override
    public String getName() {
        return "驗證退保條件";
    }
}
