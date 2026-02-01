package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.RejectEnrollmentContext;

/**
 * 拒絕報名 Task
 */
@Component
public class RejectEnrollmentTask implements PipelineTask<RejectEnrollmentContext> {

    @Override
    public void execute(RejectEnrollmentContext context) {
        String employeeName = "Employee " + context.getEnrollment().getEmployeeId(); // Placeholder
        String employeeEmail = "employee@example.com"; // Placeholder

        context.getEnrollment().reject(
                context.getOperatorId(),
                context.getRequest().getReason(),
                employeeName,
                employeeEmail,
                context.getCourse().getCourseName());
    }
}
