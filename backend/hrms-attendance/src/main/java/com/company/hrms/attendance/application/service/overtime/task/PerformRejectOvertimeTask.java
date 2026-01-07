package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.RejectOvertimeContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行加班駁回 Task
 * - 執行 OvertimeApplication.reject() 領域方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformRejectOvertimeTask implements PipelineTask<RejectOvertimeContext> {

    @Override
    public void execute(RejectOvertimeContext context) throws Exception {
        var application = context.getApplication();
        var reason = context.getRejectRequest().getReason();

        log.debug("執行加班駁回: overtimeId={}, reason={}", application.getId().getValue(), reason);

        application.reject(reason);

        log.info("加班駁回成功: overtimeId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行加班駁回";
    }

    @Override
    public boolean shouldExecute(RejectOvertimeContext context) {
        return context.getApplication() != null && context.getRejectRequest() != null;
    }
}
