package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存加保記錄 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveEnrollmentTask implements PipelineTask<EnrollmentContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        log.debug("儲存加保記錄: {} 筆", context.getEnrollments().size());

        for (InsuranceEnrollment enrollment : context.getEnrollments()) {
            enrollmentRepository.save(enrollment);
            log.debug("儲存加保記錄: type={}, id={}",
                    enrollment.getInsuranceType(), enrollment.getId().getValue());
        }

        log.info("加保記錄儲存成功: {} 筆", context.getEnrollments().size());
    }

    @Override
    public String getName() {
        return "儲存加保記錄";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return !context.getEnrollments().isEmpty();
    }
}
