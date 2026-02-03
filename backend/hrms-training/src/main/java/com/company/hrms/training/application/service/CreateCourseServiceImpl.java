package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.CreateCourseRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.application.service.context.CreateCourseContext;
import com.company.hrms.training.application.service.task.course.CreateCourseTask;
import com.company.hrms.training.application.service.task.course.SaveCourseTask;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.RequiredArgsConstructor;

/**
 * 建立課程服務
 * 使用獨立的 Task Bean 進行 Pipeline 編排
 */
@Service("createCourseServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateCourseServiceImpl implements CommandApiService<CreateCourseRequest, TrainingCourseResponse> {

    private final CreateCourseTask createCourseTask;
    private final SaveCourseTask saveCourseTask;

    @Override
    public TrainingCourseResponse execCommand(CreateCourseRequest req, JWTModel currentUser, String... args)
            throws Exception {
        CreateCourseContext ctx = new CreateCourseContext(req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(createCourseTask)
                .next(saveCourseTask)
                .execute();

        return toResponse(ctx.getTrainingCourse());
    }

    private TrainingCourseResponse toResponse(TrainingCourse course) {
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
