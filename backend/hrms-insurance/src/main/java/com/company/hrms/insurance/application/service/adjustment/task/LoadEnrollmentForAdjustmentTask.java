package com.company.hrms.insurance.application.service.adjustment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入加保記錄 Task (調整級距用)
 */
@Component("adjustmentLoadEnrollmentTask")
@RequiredArgsConstructor
@Slf4j
public class LoadEnrollmentForAdjustmentTask implements PipelineTask<AdjustmentContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(AdjustmentContext context) throws Exception {
        var enrollmentId = context.getEnrollmentId();
        log.debug("載入加保記錄: enrollmentId={}", enrollmentId.getValue());

        InsuranceEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("ENROLLMENT_NOT_FOUND",
                        "加保記錄不存在: " + enrollmentId.getValue()));

        context.setEnrollment(enrollment);
        log.info("加保記錄載入成功: type={}, currentSalary={}",
                enrollment.getInsuranceType(), enrollment.getMonthlySalary());
    }

    @Override
    public String getName() {
        return "載入加保記錄(調整)";
    }
}
