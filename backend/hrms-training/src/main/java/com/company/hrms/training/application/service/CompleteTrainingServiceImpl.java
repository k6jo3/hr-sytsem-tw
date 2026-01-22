package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.CompleteTrainingRequest;
import com.company.hrms.training.application.service.context.CompleteTrainingContext;
import com.company.hrms.training.application.task.enrollment.CompleteTrainingTask;
import com.company.hrms.training.application.task.enrollment.LoadCourseForEnrollmentActionTask;
import com.company.hrms.training.application.task.enrollment.LoadEnrollmentActionTask;
import com.company.hrms.training.application.task.enrollment.SaveEnrollmentActionTask;

import lombok.RequiredArgsConstructor;

@Service("completeTrainingServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CompleteTrainingServiceImpl implements CommandApiService<CompleteTrainingRequest, Void> {

    private final LoadEnrollmentActionTask<CompleteTrainingContext> loadEnrollmentTask;
    private final LoadCourseForEnrollmentActionTask<CompleteTrainingContext> loadCourseTask;
    private final CompleteTrainingTask completeTrainingTask;
    private final SaveEnrollmentActionTask<CompleteTrainingContext> saveEnrollmentTask;

    @Override
    public Void execCommand(CompleteTrainingRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        String enrollmentId = args[0];

        CompleteTrainingContext ctx = new CompleteTrainingContext(enrollmentId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadEnrollmentTask)
                .next(loadCourseTask)
                .next(completeTrainingTask)
                .next(saveEnrollmentTask)
                .execute();

        return null;
    }
}
