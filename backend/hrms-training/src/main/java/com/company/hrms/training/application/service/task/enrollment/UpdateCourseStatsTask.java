package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 更新課程統計 Task
 * 負責更新課程的報名人數統計
 */
@Component
@RequiredArgsConstructor
public class UpdateCourseStatsTask implements PipelineTask<EnrollCourseContext> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public void execute(EnrollCourseContext context) {
        if (context.getCourse() != null) {
            // 使用領域模型的業務方法處理
            context.getCourse().incrementEnrollmentCount();

            // 儲存更新後的課程統計
            courseRepository.save(context.getCourse());
        }
    }
}
