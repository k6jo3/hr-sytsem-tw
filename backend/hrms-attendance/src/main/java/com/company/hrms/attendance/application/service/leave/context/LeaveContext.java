package com.company.hrms.attendance.application.service.leave.context;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveContext extends PipelineContext {
    private ApplyLeaveRequest applyRequest;
    private LeaveApplication application;
    private String tenantId;

    public LeaveContext(ApplyLeaveRequest request, String tenantId) {
        this.applyRequest = request;
        this.tenantId = tenantId;
    }
}
