package com.company.hrms.attendance.application.service.correction.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.correction.context.ApproveCorrectionContext;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.extern.slf4j.Slf4j;

/**
 * 執行補卡核准 Task
 */
@Slf4j
@Component
public class PerformApproveCorrectionTask implements PipelineTask<ApproveCorrectionContext> {

    @Override
    public void execute(ApproveCorrectionContext context) throws Exception {
        var application = context.getApplication();

        application.approve();

        log.info("補卡核准成功: applicationId={}, status={}",
                application.getId().getValue(), application.getStatus());
    }

    @Override
    public String getName() {
        return "執行補卡核准";
    }

    @Override
    public boolean shouldExecute(ApproveCorrectionContext context) {
        return context.getApplication() != null;
    }
}
