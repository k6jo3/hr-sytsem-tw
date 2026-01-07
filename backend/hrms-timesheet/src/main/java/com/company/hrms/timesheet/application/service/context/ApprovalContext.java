package com.company.hrms.timesheet.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.ApproveTimesheetRequest;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApprovalContext extends PipelineContext {
    private ApproveTimesheetRequest request;
    private Timesheet timesheet;
    private ApproveTimesheetResponse response;
    private java.util.UUID approverId;

    public ApprovalContext(ApproveTimesheetRequest request) {
        this.request = request;
    }
}
