package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollmentCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入課程 Task (通用，針對報名操作)
 * 適用於 Approve, Reject, Cancel, Complete 等需要課程資訊的操作
 */
@Component
@RequiredArgsConstructor
public class LoadCourseForEnrollmentActionTask<C extends EnrollmentCourseContext> implements PipelineTask<C> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(C context) {
        TrainingCourse course = courseRepository.findById(CourseId.from(context.getEnrollment().getCourseId()))
                .orElseThrow(() -> new IllegalArgumentException("Course not found for enrollment"));
        context.setCourse(course);
    }
}
