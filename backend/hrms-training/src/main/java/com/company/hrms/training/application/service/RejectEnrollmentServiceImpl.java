package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.RejectEnrollmentRequest;
import com.company.hrms.training.application.service.context.RejectEnrollmentContext;
import com.company.hrms.training.application.task.enrollment.LoadCourseForEnrollmentActionTask;
import com.company.hrms.training.application.task.enrollment.LoadEnrollmentActionTask;
import com.company.hrms.training.application.task.enrollment.RejectEnrollmentTask;
import com.company.hrms.training.application.task.enrollment.SaveEnrollmentActionTask;

import lombok.RequiredArgsConstructor;

@Service("rejectEnrollmentServiceImpl")
@Transactional
@RequiredArgsConstructor
public class RejectEnrollmentServiceImpl implements CommandApiService<RejectEnrollmentRequest, Void> {

    private final LoadEnrollmentActionTask<RejectEnrollmentContext> loadEnrollmentTask;
    private final LoadCourseForEnrollmentActionTask<RejectEnrollmentContext> loadCourseTask;
    private final RejectEnrollmentTask rejectEnrollmentTask;
    private final SaveEnrollmentActionTask<RejectEnrollmentContext> saveEnrollmentTask;

    @Override
    public Void execCommand(RejectEnrollmentRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        String enrollmentId = args[0];

        RejectEnrollmentContext ctx = new RejectEnrollmentContext(enrollmentId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadEnrollmentTask)
                .next(loadCourseTask)
                .next(rejectEnrollmentTask)
                .next(saveEnrollmentTask)
                .execute();

        return null;
    }
}
