package com.company.hrms.attendance.application.service.leave;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApproveLeaveResponse;
import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.attendance.application.service.leave.task.LoadLeaveApplicationTask;
import com.company.hrms.attendance.application.service.leave.task.PerformApproveLeaveTask;
import com.company.hrms.attendance.application.service.leave.task.SaveApprovedLeaveTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 請假審核服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadLeaveApplicationTask - 載入請假申請</li>
 * <li>PerformApproveLeaveTask - 執行核准動作</li>
 * <li>SaveApprovedLeaveTask - 儲存已核准的申請</li>
 * </ol>
 */
@Service("approveLeaveServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApproveLeaveServiceImpl implements CommandApiService<ApproveLeaveRequest, ApproveLeaveResponse> {

    private final LoadLeaveApplicationTask loadLeaveApplicationTask;
    private final PerformApproveLeaveTask performApproveLeaveTask;
    private final SaveApprovedLeaveTask saveApprovedLeaveTask;

    @Override
    public ApproveLeaveResponse execCommand(ApproveLeaveRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("請假審核流程開始: applicationId={}", request.getApplicationId());

        ApproveLeaveContext context = new ApproveLeaveContext(request, currentUser.getTenantId());

        BusinessPipeline.start(context)
                .next(loadLeaveApplicationTask)
                .next(performApproveLeaveTask)
                .next(saveApprovedLeaveTask)
                .execute();

        log.info("請假審核流程完成: applicationId={}, status=APPROVED", request.getApplicationId());

        return ApproveLeaveResponse.approved(request.getApplicationId());
    }
}
