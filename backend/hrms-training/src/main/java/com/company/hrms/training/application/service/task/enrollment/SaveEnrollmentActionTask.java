package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollmentActionContext;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存報名記錄 Task (通用)
 * 適用於 Approve, Reject, Cancel, Confirm, Complete 等操作
 */
@Component
@RequiredArgsConstructor
public class SaveEnrollmentActionTask<C extends EnrollmentActionContext> implements PipelineTask<C> {

    private final ITrainingEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(C context) {
        enrollmentRepository.save(context.getEnrollment());
    }
}
