package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CancelEnrollmentContext;

/**
 * 取消報名 Task
 */
@Component
public class CancelEnrollmentTask implements PipelineTask<CancelEnrollmentContext> {

    @Override
    public void execute(CancelEnrollmentContext context) {
        context.getEnrollment().cancel(
                context.getOperatorId(),
                context.getRequest().getReason(),
                context.getCourse().getCourseName());
    }
}
