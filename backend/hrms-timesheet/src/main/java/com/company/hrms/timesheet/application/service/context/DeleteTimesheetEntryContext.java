package com.company.hrms.timesheet.application.service.context;

import java.util.UUID;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.DeleteTimesheetEntryRequest;
import com.company.hrms.timesheet.api.response.DeleteTimesheetEntryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeleteTimesheetEntryContext extends PipelineContext {
    private DeleteTimesheetEntryRequest request;
    private DeleteTimesheetEntryResponse response;
    private Timesheet timesheet;
    private UUID userId;

    public DeleteTimesheetEntryContext(DeleteTimesheetEntryRequest request) {
        this.request = request;
    }
}
