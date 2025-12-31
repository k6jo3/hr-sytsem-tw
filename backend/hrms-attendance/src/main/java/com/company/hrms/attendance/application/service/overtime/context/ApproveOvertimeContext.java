package com.company.hrms.attendance.application.service.overtime.context;

import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveOvertimeContext extends PipelineContext {
    private ApproveOvertimeRequest approveRequest;
    private OvertimeApplication application;
    private String tenantId;
    private String overtimeId;

    public ApproveOvertimeContext(ApproveOvertimeRequest request, String tenantId) {
        this.approveRequest = request;
        this.tenantId = tenantId;
    }
}
