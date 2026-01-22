package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.ConfirmAttendanceRequest;
import com.company.hrms.training.application.service.context.ConfirmAttendanceContext;
import com.company.hrms.training.application.task.enrollment.ConfirmAttendanceTask;
import com.company.hrms.training.application.task.enrollment.LoadEnrollmentActionTask;
import com.company.hrms.training.application.task.enrollment.SaveEnrollmentActionTask;

import lombok.RequiredArgsConstructor;

@Service("confirmAttendanceServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ConfirmAttendanceServiceImpl implements CommandApiService<ConfirmAttendanceRequest, Void> {

    private final LoadEnrollmentActionTask<ConfirmAttendanceContext> loadEnrollmentTask;
    private final ConfirmAttendanceTask confirmAttendanceTask;
    private final SaveEnrollmentActionTask<ConfirmAttendanceContext> saveEnrollmentTask;

    @Override
    public Void execCommand(ConfirmAttendanceRequest req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        String enrollmentId = args[0];

        ConfirmAttendanceContext ctx = new ConfirmAttendanceContext(enrollmentId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadEnrollmentTask)
                // ConfirmAttendance doesn't need loadCourseTask
                .next(confirmAttendanceTask)
                .next(saveEnrollmentTask)
                .execute();

        return null;
    }
}
