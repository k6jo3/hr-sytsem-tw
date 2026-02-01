package com.company.hrms.training.application.service.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.request.CreateCourseRequest;
import com.company.hrms.training.application.service.context.CreateCourseContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

/**
 * 建立課程 Task
 * 負責從 Request 建立 TrainingCourse 領域物件
 */
@Component
public class CreateCourseTask implements PipelineTask<CreateCourseContext> {

    @Override
    public void execute(CreateCourseContext context) {
        CreateCourseRequest req = context.getRequest();
        TrainingCourse course = TrainingCourse.create(
                req.getCourseCode(),
                req.getCourseName(),
                req.getCourseType(),
                req.getDeliveryMode(),
                req.getDurationHours(),
                req.getStartDate(),
                req.getEndDate(),
                context.getCreatedBy());

        // 設定選填欄位
        if (req.getCategory() != null)
            course.setCategory(req.getCategory());
        if (req.getDescription() != null)
            course.setDescription(req.getDescription());
        if (req.getInstructor() != null)
            course.setInstructor(req.getInstructor());
        if (req.getInstructorInfo() != null)
            course.setInstructorInfo(req.getInstructorInfo());
        if (req.getMaxParticipants() != null)
            course.setMaxParticipants(req.getMaxParticipants());
        if (req.getMinParticipants() != null)
            course.setMinParticipants(req.getMinParticipants());
        if (req.getStartTime() != null && req.getEndTime() != null)
            course.setTimes(req.getStartTime(), req.getEndTime());
        if (req.getLocation() != null)
            course.setLocation(req.getLocation());
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

        context.setTrainingCourse(course);
    }
}
