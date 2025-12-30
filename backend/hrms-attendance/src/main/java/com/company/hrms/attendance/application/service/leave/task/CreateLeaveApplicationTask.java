package com.company.hrms.attendance.application.service.leave.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.leave.context.LeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立請假申請 Task
 * - 建立 LeaveApplication 聚合根
 * - 設定請假相關資料
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateLeaveApplicationTask implements PipelineTask<LeaveContext> {

    @Override
    public void execute(LeaveContext context) throws Exception {
        var request = context.getApplyRequest();

        log.debug("建立請假申請: employeeId={}, leaveType={}",
                request.getEmployeeId(), request.getLeaveTypeId());

        LeaveApplication application = new LeaveApplication(
                new ApplicationId(java.util.UUID.randomUUID().toString()),
                request.getEmployeeId(),
                new LeaveTypeId(request.getLeaveTypeId()),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartPeriod() != null
                        ? LeavePeriodType.valueOf(request.getStartPeriod())
                        : LeavePeriodType.FULL_DAY,
                request.getEndPeriod() != null
                        ? LeavePeriodType.valueOf(request.getEndPeriod())
                        : LeavePeriodType.FULL_DAY,
                request.getReason());

        if (request.getProofAttachmentUrl() != null) {
            application.setProofAttachmentUrl(request.getProofAttachmentUrl());
        }

        context.setApplication(application);
        log.info("請假申請建立成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "建立請假申請";
    }

    @Override
    public boolean shouldExecute(LeaveContext context) {
        return context.getApplyRequest() != null;
    }
}
