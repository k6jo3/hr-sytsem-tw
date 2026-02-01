package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查重複報名 Task
 * 負責驗證員工是否已報名該課程
 */
@Component
@RequiredArgsConstructor
public class CheckDuplicateEnrollmentTask implements PipelineTask<EnrollCourseContext> {

    private final ITrainingEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(EnrollCourseContext context) {
        boolean exists = enrollmentRepository.existsByCourseIdAndEmployeeId(
                context.getRequest().getCourseId(),
                context.getEmployeeId());
        if (exists) {
            throw new IllegalStateException("Employee already enrolled in this course");
        }
    }
}
