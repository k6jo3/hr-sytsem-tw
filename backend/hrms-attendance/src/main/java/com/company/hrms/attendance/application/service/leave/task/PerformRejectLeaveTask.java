package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.RejectLeaveContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行請假駁回 Task
 * - 執行 LeaveApplication.reject() 領域方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformRejectLeaveTask implements PipelineTask<RejectLeaveContext> {

    @Override
    public void execute(RejectLeaveContext context) throws Exception {
        var application = context.getApplication();
        var reason = context.getRejectRequest().getReason();

        log.debug("執行請假駁回: applicationId={}, reason={}", application.getId().getValue(), reason);

        application.reject(reason);

        log.info("請假駁回成功: applicationId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行請假駁回";
    }

    @Override
    public boolean shouldExecute(RejectLeaveContext context) {
        return context.getApplication() != null && context.getRejectRequest() != null;
    }
}
