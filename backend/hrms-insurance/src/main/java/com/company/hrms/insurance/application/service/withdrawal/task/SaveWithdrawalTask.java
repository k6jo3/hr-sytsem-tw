package com.company.hrms.insurance.application.service.withdrawal.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存退保記錄 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveWithdrawalTask implements PipelineTask<WithdrawalContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(WithdrawalContext context) throws Exception {
        var enrollment = context.getEnrollment();

        log.debug("儲存退保記錄: enrollmentId={}", enrollment.getId().getValue());

        enrollmentRepository.save(enrollment);

        log.info("退保記錄儲存成功");
    }

    @Override
    public String getName() {
        return "儲存退保記錄";
    }
}
