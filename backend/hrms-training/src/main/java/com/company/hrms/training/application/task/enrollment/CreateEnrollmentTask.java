package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.request.EnrollCourseRequest;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;

/**
 * 建立報名記錄 Task
 * 負責建立 TrainingEnrollment 領域物件
 */
@Component
public class CreateEnrollmentTask implements PipelineTask<EnrollCourseContext> {

    @Override
    public void execute(EnrollCourseContext context) {
        EnrollCourseRequest req = context.getRequest();
        TrainingCourse course = context.getCourse();

        TrainingEnrollment enrollment = TrainingEnrollment.create(
                course.getId().toString(),
                course.getCourseName(),
                context.getEmployeeId(),
                context.getEmployeeName(),
                context.getManagerId(),
                context.getManagerName(),
                req.getTrainingHours() != null ? req.getTrainingHours() : course.getDurationHours(),
                req.getCost() != null ? req.getCost() : course.getCost(),
                req.getReason(),
                req.getRemarks());

        context.setEnrollment(enrollment);
    }
}
