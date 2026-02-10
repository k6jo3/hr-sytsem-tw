package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.ApproveOvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入加班申請 Task (for Approval)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadOvertimeForApprovalTask implements PipelineTask<ApproveOvertimeContext> {

    private final IOvertimeApplicationRepository repository;

    @Override
    public void execute(ApproveOvertimeContext context) throws Exception {
        String overtimeIdStr = context.getOvertimeId();
        if (overtimeIdStr == null) {
            throw new IllegalArgumentException("Overtime ID cannot be null");
        }

        OvertimeId id = new OvertimeId(overtimeIdStr);
        OvertimeApplication application = repository.findById(id)
                .orElseThrow(() -> new com.company.hrms.common.exception.EntityNotFoundException(
                        "加班申請不存在: " + overtimeIdStr));

        context.setApplication(application);
        log.debug("載入加班申請成功: {}", overtimeIdStr);
    }

    @Override
    public String getName() {
        return "載入加班申請(Approval)";
    }

    @Override
    public boolean shouldExecute(ApproveOvertimeContext context) {
        return true;
    }
}
