package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;

/**
 * 更新課程統計 Task
 * 負責更新課程的報名人數統計
 */
@Component
public class UpdateCourseStatsTask implements PipelineTask<EnrollCourseContext> {

    @Override
    public void execute(EnrollCourseContext context) {
        // TODO: 課程報名人數應由領域模型的業務方法處理
        // 例如: course.incrementEnrollmentCount()
        // 目前 currentEnrollments 可透過資料庫查詢計算取得
        // 暫時跳過此 Task 的實作
    }
}
