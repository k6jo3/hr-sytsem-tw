package com.company.hrms.timesheet.application.service.context;

import java.util.UUID;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.RejectTimesheetRequest;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RejectionContext extends PipelineContext {
    private RejectTimesheetRequest request;
    private Timesheet timesheet;
    private RejectTimesheetResponse response;
    private UUID rejectorId;

    public RejectionContext(RejectTimesheetRequest request) {
        this.request = request;
    }
}
