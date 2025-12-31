package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行請假核准 Task
 * - 執行 LeaveApplication.approve() 領域方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformApproveLeaveTask implements PipelineTask<ApproveLeaveContext> {

    @Override
    public void execute(ApproveLeaveContext context) throws Exception {
        var application = context.getApplication();

        log.debug("執行請假核准: applicationId={}", application.getId().getValue());

        application.approve();

        log.info("請假核准成功: applicationId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行請假核准";
    }

    @Override
    public boolean shouldExecute(ApproveLeaveContext context) {
        return context.getApplication() != null;
    }
}
