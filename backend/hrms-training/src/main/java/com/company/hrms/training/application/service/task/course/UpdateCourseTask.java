package com.company.hrms.training.application.service.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.request.UpdateCourseRequest;
import com.company.hrms.training.application.service.context.UpdateCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

/**
 * 更新課程資料 Task
 */
@Component
public class UpdateCourseTask implements PipelineTask<UpdateCourseContext> {

    @Override
    public void execute(UpdateCourseContext context) {
        UpdateCourseRequest req = context.getRequest();
        TrainingCourse course = context.getTrainingCourse();

        // Core update method
        course.updateInfo(
                req.getCourseName(),
                req.getDescription(),
                req.getMaxParticipants(),
                req.getLocation());

        // Update other fields
        if (req.getInstructor() != null)
            course.setInstructor(req.getInstructor());
        if (req.getInstructorInfo() != null)
            course.setInstructorInfo(req.getInstructorInfo());
        if (req.getMinParticipants() != null)
            course.setMinParticipants(req.getMinParticipants());
        if (req.getStartTime() != null && req.getEndTime() != null)
            course.setTimes(req.getStartTime(), req.getEndTime());
        if (req.getCost() != null)
            course.setCost(req.getCost());
        if (req.getIsMandatory() != null)
            course.setMandatory(req.getIsMandatory());
        if (req.getTargetAudience() != null)
            course.setTargetAudience(req.getTargetAudience());
        if (req.getPrerequisites() != null)
            course.setPrerequisites(req.getPrerequisites());
        if (req.getEnrollmentDeadline() != null)
            course.setEnrollmentDeadline(req.getEnrollmentDeadline());
    }
}
