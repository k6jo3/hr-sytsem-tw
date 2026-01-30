package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.ApproveEnrollmentContext;

/**
 * 執行審核報名 Task
 */
@Component
public class ApproveEnrollmentTask implements PipelineTask<ApproveEnrollmentContext> {

    @Override
    public void execute(ApproveEnrollmentContext context) {
        // Placeholder for employee info
        String employeeName = "Employee " + context.getEnrollment().getEmployeeId();
        String employeeEmail = "employee@example.com";

        context.getEnrollment().approve(
                context.getOperatorId(),
                context.getCourse().getCourseName(),
                employeeName,
                employeeEmail,
                context.getCourse().getStartDate().toString(),
                context.getCourse().getLocation());
    }
}
