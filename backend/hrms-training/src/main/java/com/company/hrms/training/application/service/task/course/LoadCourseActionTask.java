package com.company.hrms.training.application.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CourseActionContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入課程 Task (通用動作)
 * 適用於 Publish, Close, Complete 等操作
 */
@Component
@RequiredArgsConstructor
public class LoadCourseActionTask implements PipelineTask<CourseActionContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(CourseActionContext context) {
        TrainingCourse course = courseRepository.findById(CourseId.from(context.getCourseId()))
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + context.getCourseId()));
        context.setCourse(course);
    }
}
