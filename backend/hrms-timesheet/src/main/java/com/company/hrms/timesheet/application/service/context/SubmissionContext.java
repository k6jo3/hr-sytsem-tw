package com.company.hrms.timesheet.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.SubmitTimesheetRequest;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubmissionContext extends PipelineContext {
    private SubmitTimesheetRequest request;
    private Timesheet timesheet;
    private SubmitTimesheetResponse response;

    public SubmissionContext(SubmitTimesheetRequest request) {
        this.request = request;
    }
}
