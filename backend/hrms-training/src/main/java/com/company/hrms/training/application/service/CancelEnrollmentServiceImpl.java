package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.CancelEnrollmentRequest;
import com.company.hrms.training.application.service.context.CancelEnrollmentContext;
import com.company.hrms.training.application.service.task.enrollment.CancelEnrollmentTask;
import com.company.hrms.training.application.service.task.enrollment.LoadCourseForEnrollmentActionTask;
import com.company.hrms.training.application.service.task.enrollment.LoadEnrollmentActionTask;
import com.company.hrms.training.application.service.task.enrollment.SaveEnrollmentActionTask;

import lombok.RequiredArgsConstructor;

@Service("cancelEnrollmentServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CancelEnrollmentServiceImpl implements CommandApiService<CancelEnrollmentRequest, Void> {

    private final LoadEnrollmentActionTask<CancelEnrollmentContext> loadEnrollmentTask;
    private final LoadCourseForEnrollmentActionTask<CancelEnrollmentContext> loadCourseTask;
    private final CancelEnrollmentTask cancelEnrollmentTask;
    private final SaveEnrollmentActionTask<CancelEnrollmentContext> saveEnrollmentTask;

    @Override
    public Void execCommand(CancelEnrollmentRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        String enrollmentId = args[0];

        CancelEnrollmentContext ctx = new CancelEnrollmentContext(enrollmentId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadEnrollmentTask)
                .next(loadCourseTask)
                .next(cancelEnrollmentTask)
                .next(saveEnrollmentTask)
                .execute();

        return null;
    }
}
