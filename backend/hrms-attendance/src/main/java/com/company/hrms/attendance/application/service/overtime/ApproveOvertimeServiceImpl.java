package com.company.hrms.attendance.application.service.overtime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.ApproveOvertimeResponse;
import com.company.hrms.attendance.application.service.overtime.context.ApproveOvertimeContext;
import com.company.hrms.attendance.application.service.overtime.task.LoadOvertimeForApprovalTask;
import com.company.hrms.attendance.application.service.overtime.task.PerformApproveOvertimeTask;
import com.company.hrms.attendance.application.service.overtime.task.SaveOvertimeForApprovalTask;
import com.company.hrms.attendance.domain.event.OvertimeApprovedEvent;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 加班核准服務實作
 */
@Service("approveOvertimeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApproveOvertimeServiceImpl implements CommandApiService<ApproveOvertimeRequest, ApproveOvertimeResponse> {

    private final LoadOvertimeForApprovalTask loadOvertimeForApprovalTask;
    private final PerformApproveOvertimeTask performApproveOvertimeTask;
    private final SaveOvertimeForApprovalTask saveOvertimeForApprovalTask;
    private final EventPublisher eventPublisher;

    @Override
    public ApproveOvertimeResponse execCommand(ApproveOvertimeRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String overtimeId = args.length > 0 ? args[0] : null;
        if (overtimeId == null) {
            throw new IllegalArgumentException("Overtime ID is required");
        }

        log.info("加班核准流程開始: overtimeId={}", overtimeId);

        ApproveOvertimeContext context = new ApproveOvertimeContext(request, currentUser.getTenantId());
        context.setOvertimeId(overtimeId);

        BusinessPipeline.start(context)
                .next(loadOvertimeForApprovalTask)
                .next(performApproveOvertimeTask)
                .next(saveOvertimeForApprovalTask)
                .execute();

        log.info("加班核准流程完成: overtimeId={}, status={}",
                context.getApplication().getId().getValue(),
                context.getApplication().getStatus());

        // 發布領域事件
        eventPublisher.publish(new OvertimeApprovedEvent(
                context.getApplication().getId().getValue(),
                currentUser.getUserId()));

        return ApproveOvertimeResponse.success(context.getApplication().getId().getValue());
    }
}
