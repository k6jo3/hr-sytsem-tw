package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.ConfirmAttendanceContext;

/**
 * 確認出席 Task
 */
@Component
public class ConfirmAttendanceTask implements PipelineTask<ConfirmAttendanceContext> {

    @Override
    public void execute(ConfirmAttendanceContext context) {
        context.getEnrollment().confirmAttendance(
                context.getRequest().getAttended(),
                context.getRequest().getAttendedHours(),
                context.getRequest().getRemarks());
    }
}
