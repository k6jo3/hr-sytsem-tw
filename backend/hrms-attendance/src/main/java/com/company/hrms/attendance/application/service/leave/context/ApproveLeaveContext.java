package com.company.hrms.attendance.application.service.leave.context;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveLeaveContext extends PipelineContext {
    private ApproveLeaveRequest approveRequest;
    private LeaveApplication application;
    private String tenantId;

    public ApproveLeaveContext(ApproveLeaveRequest request, String tenantId) {
        this.approveRequest = request;
        this.tenantId = tenantId;
    }
}
