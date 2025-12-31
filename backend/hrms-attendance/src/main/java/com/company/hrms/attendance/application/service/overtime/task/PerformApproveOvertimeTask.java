package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.ApproveOvertimeContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行加班核准 Task
 * - 執行 OvertimeApplication.approve() 領域方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformApproveOvertimeTask implements PipelineTask<ApproveOvertimeContext> {

    @Override
    public void execute(ApproveOvertimeContext context) throws Exception {
        var application = context.getApplication();

        log.debug("執行加班核准: overtimeId={}", application.getId().getValue());

        application.approve();

        log.info("加班核准成功: overtimeId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行加班核准";
    }

    @Override
    public boolean shouldExecute(ApproveOvertimeContext context) {
        return context.getApplication() != null;
    }
}
