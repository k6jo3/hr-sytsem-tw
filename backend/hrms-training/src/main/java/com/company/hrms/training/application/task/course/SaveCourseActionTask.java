package com.company.hrms.training.application.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CourseActionContext;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存課程 Task (通用動作)
 * 適用於 Publish, Close, Complete 等操作
 */
@Component
@RequiredArgsConstructor
public class SaveCourseActionTask implements PipelineTask<CourseActionContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(CourseActionContext context) {
        courseRepository.save(context.getCourse());
    }
}
