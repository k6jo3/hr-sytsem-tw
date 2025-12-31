package com.company.hrms.attendance.application.service.overtime.context;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OvertimeContext extends PipelineContext {
    private ApplyOvertimeRequest applyRequest;
    private OvertimeApplication application;
    private String tenantId;

    public OvertimeContext(ApplyOvertimeRequest request, String tenantId) {
        this.applyRequest = request;
        this.tenantId = tenantId;
    }
}
