package com.company.hrms.attendance.application.service.leave.context;

import com.company.hrms.attendance.api.request.leave.RejectLeaveRequest;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectLeaveContext extends PipelineContext {
    private RejectLeaveRequest rejectRequest;
    private LeaveApplication application;
    private String tenantId;
    private String applicationId;

    public RejectLeaveContext(RejectLeaveRequest request, String tenantId) {
        this.rejectRequest = request;
        this.tenantId = tenantId;
    }
}
