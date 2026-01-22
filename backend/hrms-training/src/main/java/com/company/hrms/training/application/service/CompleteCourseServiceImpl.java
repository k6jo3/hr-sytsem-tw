package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.CourseActionRequest;
import com.company.hrms.training.application.service.context.CourseActionContext;
import com.company.hrms.training.application.task.course.CompleteCourseTask;
import com.company.hrms.training.application.task.course.LoadCourseActionTask;
import com.company.hrms.training.application.task.course.SaveCourseActionTask;

import lombok.RequiredArgsConstructor;

@Service("completeCourseServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CompleteCourseServiceImpl implements CommandApiService<CourseActionRequest, Void> {

    private final LoadCourseActionTask loadCourseTask;
    private final CompleteCourseTask completeCourseTask;
    private final SaveCourseActionTask saveCourseTask;

    @Override
    public Void execCommand(CourseActionRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Course ID is required");
        }
        String courseId = args[0];

        CourseActionContext ctx = new CourseActionContext(courseId, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadCourseTask)
                .next(completeCourseTask)
                .next(saveCourseTask)
                .execute();

        return null;
    }
}
