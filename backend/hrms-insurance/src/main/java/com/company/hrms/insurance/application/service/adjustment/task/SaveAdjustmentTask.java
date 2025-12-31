package com.company.hrms.insurance.application.service.adjustment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存調整記錄 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveAdjustmentTask implements PipelineTask<AdjustmentContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(AdjustmentContext context) throws Exception {
        var enrollment = context.getEnrollment();

        log.debug("儲存調整記錄: enrollmentId={}", enrollment.getId().getValue());

        enrollmentRepository.save(enrollment);

        log.info("調整記錄儲存成功");
    }

    @Override
    public String getName() {
        return "儲存調整記錄";
    }
}
