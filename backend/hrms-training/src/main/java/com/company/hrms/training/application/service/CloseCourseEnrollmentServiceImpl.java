package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.CloseCourseRequest;
import com.company.hrms.training.application.service.context.CourseActionContext;
import com.company.hrms.training.application.service.task.course.CloseCourseTask;
import com.company.hrms.training.application.service.task.course.LoadCourseActionTask;
import com.company.hrms.training.application.service.task.course.SaveCourseActionTask;

import lombok.RequiredArgsConstructor;

@Service("closeCourseEnrollmentServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CloseCourseEnrollmentServiceImpl implements CommandApiService<CloseCourseRequest, Void> {

    private final LoadCourseActionTask loadCourseTask;
    private final CloseCourseTask closeCourseTask;
    private final SaveCourseActionTask saveCourseTask;

    @Override
    public Void execCommand(CloseCourseRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Course ID is required");
        }
        String courseId = args[0];

        CourseActionContext ctx = new CourseActionContext(courseId, currentUser.getUserId());
        ctx.setReason(req.getReason());

        BusinessPipeline.start(ctx)
                .next(loadCourseTask)
                .next(closeCourseTask)
                .next(saveCourseTask)
                .execute();

        return null;
    }
}
