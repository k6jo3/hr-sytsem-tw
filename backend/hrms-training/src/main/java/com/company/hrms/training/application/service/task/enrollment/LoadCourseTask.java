package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入課程 Task
 * 負責驗證課程是否存在且可報名
 */
@Component
@RequiredArgsConstructor
public class LoadCourseTask implements PipelineTask<EnrollCourseContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(EnrollCourseContext context) {
        String courseId = context.getRequest().getCourseId();
        TrainingCourse course = courseRepository.findById(CourseId.from(courseId))
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (!course.canEnroll()) {
            throw new IllegalStateException("Course is not open for enrollment or full/expired");
        }
        context.setCourse(course);
    }
}
