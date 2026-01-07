package com.company.hrms.timesheet.application.service.context;

import java.util.UUID;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.timesheet.api.request.UpdateTimesheetEntryRequest;
import com.company.hrms.timesheet.api.response.UpdateTimesheetEntryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateTimesheetEntryContext extends PipelineContext {
    private UpdateTimesheetEntryRequest request;
    private UpdateTimesheetEntryResponse response;
    private Timesheet timesheet;
    private UUID userId;

    public UpdateTimesheetEntryContext(UpdateTimesheetEntryRequest request) {
        this.request = request;
    }
}
