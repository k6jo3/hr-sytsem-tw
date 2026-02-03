package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.response.UpdateTimesheetEntryResponse;
import com.company.hrms.timesheet.application.service.context.UpdateTimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateEntryTask implements PipelineTask<UpdateTimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(UpdateTimesheetEntryContext context) {
        Timesheet timesheet = context.getTimesheet();
        var request = context.getRequest();

        // Use the new updateEntry method to keep the same ID
        TimesheetEntry updatedEntry = new TimesheetEntry();
        updatedEntry.setProjectId(request.getProjectId());
        updatedEntry.setTaskId(request.getTaskId());
        updatedEntry.setWorkDate(request.getWorkDate());
        updatedEntry.setHours(request.getHours());
        updatedEntry.setDescription(request.getDescription());

        timesheet.updateEntry(request.getEntryId(), updatedEntry);

        // Save
        timesheetRepository.save(timesheet);

        // Set Response (Return the same ID)
        context.setResponse(UpdateTimesheetEntryResponse.builder()
                .entryId(request.getEntryId().toString())
                .build());
    }
}
