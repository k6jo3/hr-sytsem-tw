package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存報名記錄 Task
 * 負責將報名記錄儲存至資料庫
 */
@Component
@RequiredArgsConstructor
public class SaveEnrollmentTask implements PipelineTask<EnrollCourseContext> {

    private final ITrainingEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(EnrollCourseContext context) {
        enrollmentRepository.save(context.getEnrollment());
    }
}
