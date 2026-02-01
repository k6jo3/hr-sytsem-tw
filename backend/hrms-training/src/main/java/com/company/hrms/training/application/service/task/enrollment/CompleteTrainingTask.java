package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CompleteTrainingContext;

/**
 * 完成訓練 Task
 */
@Component
public class CompleteTrainingTask implements PipelineTask<CompleteTrainingContext> {

    @Override
    public void execute(CompleteTrainingContext context) {
        String employeeName = "Employee " + context.getEnrollment().getEmployeeId(); // Placeholder

        context.getEnrollment().complete(
                context.getRequest().getCompletedHours(),
                context.getRequest().getScore(),
                context.getRequest().getPassed(),
                context.getRequest().getFeedback(),
                employeeName,
                context.getCourse().getCourseName(),
                context.getCourse().getCategory() != null ? context.getCourse().getCategory().name() : "GENERAL");
    }
}
