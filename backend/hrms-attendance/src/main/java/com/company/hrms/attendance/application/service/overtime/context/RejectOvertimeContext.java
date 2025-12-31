package com.company.hrms.attendance.application.service.overtime.context;

import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectOvertimeContext extends PipelineContext {
    private RejectOvertimeRequest rejectRequest;
    private OvertimeApplication application;
    private String tenantId;
    private String overtimeId;

    public RejectOvertimeContext(RejectOvertimeRequest request, String tenantId) {
        this.rejectRequest = request;
        this.tenantId = tenantId;
    }
}
