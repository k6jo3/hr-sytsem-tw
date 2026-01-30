package com.company.hrms.training.application.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.UpdateCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入課程(更新用) Task
 */
@Component
@RequiredArgsConstructor
public class LoadCourseForUpdateTask implements PipelineTask<UpdateCourseContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(UpdateCourseContext context) {
        TrainingCourse course = courseRepository.findById(CourseId.from(context.getCourseId()))
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + context.getCourseId()));
        context.setTrainingCourse(course);
    }
}
