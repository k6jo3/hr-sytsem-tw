package com.company.hrms.attendance.application.service.leave.context;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelLeaveContext extends PipelineContext {
    private LeaveApplication application;
    private String tenantId;
    private String applicationId;

    public CancelLeaveContext(String tenantId) {
        this.tenantId = tenantId;
    }
}
