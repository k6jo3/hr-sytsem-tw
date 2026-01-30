package com.company.hrms.training.application.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CreateCourseContext;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存課程 Task
 * 負責驗證課程代碼唯一性並儲存課程
 */
@Component
@RequiredArgsConstructor
public class SaveCourseTask implements PipelineTask<CreateCourseContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(CreateCourseContext context) {
        // 驗證課程代碼唯一性
        if (courseRepository.existsByCourseCode(context.getRequest().getCourseCode())) {
            throw new IllegalArgumentException("課程代碼已存在: " + context.getRequest().getCourseCode());
        }
        // 儲存課程
        courseRepository.save(context.getTrainingCourse());
    }
}
