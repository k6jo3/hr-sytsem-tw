package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.RejectOvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入加班申請 Task (for Rejection)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadOvertimeForRejectionTask implements PipelineTask<RejectOvertimeContext> {

    private final IOvertimeApplicationRepository repository;

    @Override
    public void execute(RejectOvertimeContext context) throws Exception {
        String overtimeIdStr = context.getOvertimeId();
        if (overtimeIdStr == null) {
            throw new IllegalArgumentException("Overtime ID cannot be null");
        }

        OvertimeId id = new OvertimeId(overtimeIdStr);
        OvertimeApplication application = repository.findById(id)
                .orElseThrow(() -> new DomainException("OVERTIME_NOT_FOUND", "加班申請不存在: " + overtimeIdStr));

        context.setApplication(application);
        log.debug("載入加班申請成功(Reject): {}", overtimeIdStr);
    }

    @Override
    public String getName() {
        return "載入加班申請(Reject)";
    }

    @Override
    public boolean shouldExecute(RejectOvertimeContext context) {
        return true;
    }
}
