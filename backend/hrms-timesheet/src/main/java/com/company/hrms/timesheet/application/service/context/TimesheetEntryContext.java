package com.company.hrms.timesheet.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TimesheetEntryContext extends PipelineContext {
    private CreateEntryRequest request;
    private Timesheet timesheet;
    private CreateEntryResponse response;

    public TimesheetEntryContext(CreateEntryRequest request) {
        this.request = request;
    }
}
