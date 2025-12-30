package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存已核准的請假申請 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveApprovedLeaveTask implements PipelineTask<ApproveLeaveContext> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public void execute(ApproveLeaveContext context) throws Exception {
        var application = context.getApplication();
        log.debug("儲存已核准的請假申請: applicationId={}", application.getId().getValue());

        leaveApplicationRepository.save(application);

        log.info("已核准請假申請儲存成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存已核准請假申請";
    }

    @Override
    public boolean shouldExecute(ApproveLeaveContext context) {
        return context.getApplication() != null;
    }
}
