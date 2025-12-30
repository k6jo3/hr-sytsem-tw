package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入請假申請 Task
 * - 根據 applicationId 載入請假申請
 * - 驗證申請是否存在
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadLeaveApplicationTask implements PipelineTask<ApproveLeaveContext> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public void execute(ApproveLeaveContext context) throws Exception {
        ApproveLeaveRequest request = context.getApproveRequest();
        String applicationId = request.getApplicationId();

        log.debug("載入請假申請: applicationId={}", applicationId);

        LeaveApplication application = leaveApplicationRepository
                .findById(new ApplicationId(applicationId))
                .orElseThrow(() -> new IllegalArgumentException("找不到請假申請: " + applicationId));

        context.setApplication(application);
        log.info("請假申請載入成功: applicationId={}, status={}", applicationId, application.getStatus());
    }

    @Override
    public String getName() {
        return "載入請假申請";
    }

    @Override
    public boolean shouldExecute(ApproveLeaveContext context) {
        return context.getApproveRequest() != null;
    }
}
