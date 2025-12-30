package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.LeaveContext;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存請假申請 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveLeaveApplicationTask implements PipelineTask<LeaveContext> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public void execute(LeaveContext context) throws Exception {
        var application = context.getApplication();
        log.debug("儲存請假申請: applicationId={}", application.getId().getValue());

        leaveApplicationRepository.save(application);

        log.info("請假申請儲存成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存請假申請";
    }

    @Override
    public boolean shouldExecute(LeaveContext context) {
        return context.getApplication() != null;
    }
}
