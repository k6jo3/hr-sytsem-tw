package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.EnrollCourseRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.application.task.enrollment.CheckDuplicateEnrollmentTask;
import com.company.hrms.training.application.task.enrollment.CreateEnrollmentTask;
import com.company.hrms.training.application.task.enrollment.FetchEmployeeInfoTask;
import com.company.hrms.training.application.task.enrollment.LoadCourseTask;
import com.company.hrms.training.application.task.enrollment.SaveEnrollmentTask;
import com.company.hrms.training.application.task.enrollment.UpdateCourseStatsTask;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;

import lombok.RequiredArgsConstructor;

/**
 * 報名課程服務
 * 使用獨立的 Task Bean 進行 Pipeline 編排
 */
@Service("enrollCourseServiceImpl")
@Transactional
@RequiredArgsConstructor
public class EnrollCourseServiceImpl implements CommandApiService<EnrollCourseRequest, TrainingEnrollmentResponse> {

    private final LoadCourseTask loadCourseTask;
    private final CheckDuplicateEnrollmentTask checkDuplicateTask;
    private final FetchEmployeeInfoTask fetchEmployeeInfoTask;
    private final CreateEnrollmentTask createEnrollmentTask;
    private final SaveEnrollmentTask saveEnrollmentTask;
    private final UpdateCourseStatsTask updateCourseStatsTask;

    @Override
    public TrainingEnrollmentResponse execCommand(EnrollCourseRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // 決定目標員工：若請求有 employeeId 則使用 (管理員代為報名)，否則使用當前用戶
        String targetEmployeeId = (req.getEmployeeId() != null && !req.getEmployeeId().isEmpty())
                ? req.getEmployeeId()
                : currentUser.getUserId();

        EnrollCourseContext ctx = new EnrollCourseContext(req, targetEmployeeId, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadCourseTask)
                .next(checkDuplicateTask)
                .next(fetchEmployeeInfoTask)
                .next(createEnrollmentTask)
                .next(saveEnrollmentTask)
                .next(updateCourseStatsTask)
                .execute();

        return toResponse(ctx.getEnrollment());
    }

    private TrainingEnrollmentResponse toResponse(TrainingEnrollment enrollment) {
        TrainingEnrollmentResponse res = new TrainingEnrollmentResponse();
        res.setEnrollmentId(enrollment.getId().toString());
        res.setCourseId(enrollment.getCourseId());
        res.setEmployeeId(enrollment.getEmployeeId());
        res.setStatus(enrollment.getStatus());
        res.setReason(enrollment.getReason());
        res.setRemarks(enrollment.getRemarks());
        res.setApprovedBy(enrollment.getApprovedBy());
        res.setApprovedAt(enrollment.getApprovedAt());
        res.setRejectedBy(enrollment.getRejectedBy());
        res.setRejectedAt(enrollment.getRejectedAt());
        res.setRejectReason(enrollment.getRejectReason());
        res.setCancelledBy(enrollment.getCancelledBy());
        res.setCancelledAt(enrollment.getCancelledAt());
        res.setCancelReason(enrollment.getCancelReason());
        res.setAttendance(enrollment.isAttendance());
        res.setAttendedHours(enrollment.getAttendedHours());
        res.setCompletedHours(enrollment.getCompletedHours());
        res.setScore(enrollment.getScore());
        res.setPassed(enrollment.getPassed());
        res.setFeedback(enrollment.getFeedback());
        res.setCompletedAt(enrollment.getCompletedAt());
        res.setCreatedAt(enrollment.getCreatedAt());
        res.setUpdatedAt(enrollment.getUpdatedAt());
        return res;
    }
}
