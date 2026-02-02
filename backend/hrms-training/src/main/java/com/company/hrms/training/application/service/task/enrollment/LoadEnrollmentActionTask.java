package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollmentActionContext;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入報名記錄 Task (通用)
 * 適用於 Approve, Reject, Cancel, Confirm, Complete 等操作
 */
@Component
@RequiredArgsConstructor
public class LoadEnrollmentActionTask<C extends EnrollmentActionContext> implements PipelineTask<C> {

    private final ITrainingEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(C context) {
        TrainingEnrollment enrollment = enrollmentRepository.findById(EnrollmentId.from(context.getEnrollmentId()))
                .orElseThrow(
                        () -> new IllegalArgumentException("Enrollment not found: " + context.getEnrollmentId()));
        context.setEnrollment(enrollment);
    }
}
