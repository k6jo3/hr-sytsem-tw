package com.company.hrms.training.application.service.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.UpdateCourseContext;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存更新後的課程 Task
 */
@Component
@RequiredArgsConstructor
public class SaveUpdatedCourseTask implements PipelineTask<UpdateCourseContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(UpdateCourseContext context) {
        courseRepository.save(context.getTrainingCourse());
    }
}
