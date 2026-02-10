package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.ApproveOvertimeContext;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存加班申請 Task (for Approval)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveOvertimeForApprovalTask implements PipelineTask<ApproveOvertimeContext> {

    private final IOvertimeApplicationRepository overtimeApplicationRepository;

    @Override
    public void execute(ApproveOvertimeContext context) throws Exception {
        var application = context.getApplication();
        log.debug("儲存加班申請(Approval): applicationId={}", application.getId().getValue());

        overtimeApplicationRepository.save(application);

        log.info("加班申請儲存成功(Approval): applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存加班申請(Approval)";
    }

    @Override
    public boolean shouldExecute(ApproveOvertimeContext context) {
        return context.getApplication() != null;
    }
}
