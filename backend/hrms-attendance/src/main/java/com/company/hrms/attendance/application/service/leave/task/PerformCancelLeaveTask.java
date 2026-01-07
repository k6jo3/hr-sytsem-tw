package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.CancelLeaveContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行請假取消 Task
 * - 執行 LeaveApplication.cancel() 領域方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformCancelLeaveTask implements PipelineTask<CancelLeaveContext> {

    @Override
    public void execute(CancelLeaveContext context) throws Exception {
        var application = context.getApplication();

        log.debug("執行請假取消: applicationId={}", application.getId().getValue());

        application.cancel();

        log.info("請假取消成功: applicationId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行請假取消";
    }

    @Override
    public boolean shouldExecute(CancelLeaveContext context) {
        return context.getApplication() != null;
    }
}
