package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.UpdateCourseRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.application.service.context.UpdateCourseContext;
import com.company.hrms.training.application.task.course.LoadCourseForUpdateTask;
import com.company.hrms.training.application.task.course.SaveUpdatedCourseTask;
import com.company.hrms.training.application.task.course.UpdateCourseTask;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.RequiredArgsConstructor;

@Service("updateCourseServiceImpl")
@Transactional
@RequiredArgsConstructor
public class UpdateCourseServiceImpl implements CommandApiService<UpdateCourseRequest, TrainingCourseResponse> {

    private final LoadCourseForUpdateTask loadCourseTask;
    private final UpdateCourseTask updateCourseTask;
    private final SaveUpdatedCourseTask saveCourseTask;

    @Override
    public TrainingCourseResponse execCommand(UpdateCourseRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Course ID is required");
        }
        String courseId = args[0];

        UpdateCourseContext ctx = new UpdateCourseContext(courseId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadCourseTask)
                .next(updateCourseTask)
                .next(saveCourseTask)
                .execute();

        return toResponse(ctx.getTrainingCourse());
    }

    private TrainingCourseResponse toResponse(TrainingCourse course) {
        // TODO: 程式太長，建議用objectMapper或structMapper
        TrainingCourseResponse res = new TrainingCourseResponse();
        res.setCourseId(course.getId().toString());
        res.setCourseCode(course.getCourseCode());
        res.setCourseName(course.getCourseName());
        res.setCourseType(course.getCourseType());
        res.setDeliveryMode(course.getDeliveryMode());
        res.setCategory(course.getCategory());
        res.setDescription(course.getDescription());
        res.setInstructor(course.getInstructor());
        res.setInstructorInfo(course.getInstructorInfo());
        res.setDurationHours(course.getDurationHours());
        res.setMaxParticipants(course.getMaxParticipants());
        res.setMinParticipants(course.getMinParticipants());
        res.setCurrentEnrollments(course.getCurrentEnrollments());
        res.setStartDate(course.getStartDate());
        res.setEndDate(course.getEndDate());
        res.setStartTime(course.getStartTime());
        res.setEndTime(course.getEndTime());
        res.setLocation(course.getLocation());
        res.setCost(course.getCost());
        res.setIsMandatory(course.getIsMandatory());
        res.setTargetAudience(course.getTargetAudience());
        res.setPrerequisites(course.getPrerequisites());
        res.setEnrollmentDeadline(course.getEnrollmentDeadline());
        res.setStatus(course.getStatus());
        res.setCreatedBy(course.getCreatedBy());
        return res;
    }
}
