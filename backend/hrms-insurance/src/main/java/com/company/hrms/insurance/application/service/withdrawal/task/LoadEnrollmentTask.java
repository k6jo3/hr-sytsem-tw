package com.company.hrms.insurance.application.service.withdrawal.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入加保記錄 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadEnrollmentTask implements PipelineTask<WithdrawalContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(WithdrawalContext context) throws Exception {
        var enrollmentId = context.getEnrollmentId();
        log.debug("載入加保記錄: enrollmentId={}", enrollmentId.getValue());

        InsuranceEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("加保記錄不存在: " + enrollmentId.getValue()));

        context.setEnrollment(enrollment);
        log.info("加保記錄載入成功: type={}, employeeId={}",
                enrollment.getInsuranceType(), enrollment.getEmployeeId());
    }

    @Override
    public String getName() {
        return "載入加保記錄";
    }
}
