package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.UpdateCourseRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.application.service.context.UpdateCourseContext;
import com.company.hrms.training.application.service.task.course.LoadCourseForUpdateTask;
import com.company.hrms.training.application.service.task.course.SaveUpdatedCourseTask;
import com.company.hrms.training.application.service.task.course.UpdateCourseTask;
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
        return com.company.hrms.training.application.assembler.TrainingCourseAssembler.toResponse(course);
    }
}

