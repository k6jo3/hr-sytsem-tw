package com.company.hrms.training.application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.EnrollCourseRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.application.service.task.enrollment.CheckDuplicateEnrollmentTask;
import com.company.hrms.training.application.service.task.enrollment.CreateEnrollmentTask;
import com.company.hrms.training.application.service.task.enrollment.LoadCourseTask;
import com.company.hrms.training.application.service.task.enrollment.SaveEnrollmentTask;
import com.company.hrms.training.application.service.task.enrollment.UpdateCourseStatsTask;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;

@Service("enrollCourseServiceImpl")
@Transactional
public class EnrollCourseServiceImpl implements CommandApiService<EnrollCourseRequest, TrainingEnrollmentResponse> {

    @Autowired
    private LoadCourseTask loadCourseTask;

    @Autowired
    private CheckDuplicateEnrollmentTask checkDuplicateEnrollmentTask;

    @Autowired
    private CreateEnrollmentTask createEnrollmentTask;

    @Autowired
    private SaveEnrollmentTask saveEnrollmentTask;

    @Autowired
    private UpdateCourseStatsTask updateCourseStatsTask;

    @Override
    public TrainingEnrollmentResponse execCommand(EnrollCourseRequest request, JWTModel currentUser, String... args)
            throws Exception {
        // Determine target employee ID.
        // Using employeeNumber from JWT as default if request doesn't specify.
        String targetEmployeeId = Optional.ofNullable(request.getEmployeeId())
                .filter(s -> !s.trim().isEmpty())
                .orElse(currentUser.getEmployeeNumber());

        // Constructor expects: (EnrollCourseRequest request, String employeeId, String
        // requestedBy)
        EnrollCourseContext ctx = new EnrollCourseContext(
                request,
                targetEmployeeId,
                currentUser.getUserId());

        // Set the full user model for tasks to use
        ctx.setCurrentUser(currentUser);

        BusinessPipeline.start(ctx)
                .next(loadCourseTask)
                .next(checkDuplicateEnrollmentTask)
                .next(createEnrollmentTask)
                .next(saveEnrollmentTask)
                .next(updateCourseStatsTask)
                .execute();

        return toResponse(ctx.getEnrollment());
    }

    private TrainingEnrollmentResponse toResponse(TrainingEnrollment enrollment) {
        if (enrollment == null) {
            return new TrainingEnrollmentResponse();
        }
        TrainingEnrollmentResponse res = new TrainingEnrollmentResponse();

        // Map ID. EnrollmentId is a ValueObject, usually has toString that returns the
        // UUID value.
        if (enrollment.getId() != null) {
            res.setEnrollmentId(enrollment.getId().toString());
        }

        res.setCourseId(enrollment.getCourseId());
        res.setEmployeeId(enrollment.getEmployeeId());

        // Map common audit fields
        res.setCreatedAt(enrollment.getCreatedAt());
        res.setUpdatedAt(enrollment.getUpdatedAt());

        // Map Status (Enum to Enum)
        res.setStatus(enrollment.getStatus());

        // Map other details provided in TrainingEnrollmentResponse
        res.setReason(enrollment.getReason());
        res.setRemarks(enrollment.getRemarks());
        res.setPassed(enrollment.getPassed());
        res.setScore(enrollment.getScore());
        res.setCompletedAt(enrollment.getCompletedAt());

        return res;
    }
}
