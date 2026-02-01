package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.ApproveEnrollmentRequest;
import com.company.hrms.training.application.service.context.ApproveEnrollmentContext;
import com.company.hrms.training.application.service.task.enrollment.ApproveEnrollmentTask;
import com.company.hrms.training.application.service.task.enrollment.LoadCourseForEnrollmentActionTask;
import com.company.hrms.training.application.service.task.enrollment.LoadEnrollmentActionTask;
import com.company.hrms.training.application.service.task.enrollment.SaveEnrollmentActionTask;

import lombok.RequiredArgsConstructor;

@Service("approveEnrollmentServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ApproveEnrollmentServiceImpl implements CommandApiService<ApproveEnrollmentRequest, Void> {

    private final LoadEnrollmentActionTask<ApproveEnrollmentContext> loadEnrollmentTask;
    private final LoadCourseForEnrollmentActionTask<ApproveEnrollmentContext> loadCourseTask;
    private final ApproveEnrollmentTask approveEnrollmentTask;
    private final SaveEnrollmentActionTask<ApproveEnrollmentContext> saveEnrollmentTask;

    @Override
    public Void execCommand(ApproveEnrollmentRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        String enrollmentId = args[0];

        ApproveEnrollmentContext ctx = new ApproveEnrollmentContext(enrollmentId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadEnrollmentTask)
                .next(loadCourseTask)
                .next(approveEnrollmentTask)
                .next(saveEnrollmentTask)
                .execute();

        return null;
    }
}
