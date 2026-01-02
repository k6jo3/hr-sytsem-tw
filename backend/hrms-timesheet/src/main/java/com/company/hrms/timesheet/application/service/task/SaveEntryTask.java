package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.application.service.context.TimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveEntryTask implements PipelineTask<TimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(TimesheetEntryContext context) {
        Timesheet timesheet = context.getTimesheet();
        CreateEntryRequest request = context.getRequest();

        TimesheetEntry entry = TimesheetEntry.create(
                request.getProjectId(),
                request.getWorkDate(),
                request.getHours(),
                request.getDescription());
        // Set optional taskId
        entry.setTaskId(request.getTaskId());

        timesheet.addEntry(entry);
        timesheetRepository.save(timesheet);

        CreateEntryResponse response = CreateEntryResponse.builder()
                .timesheetId(timesheet.getId().getValue())
                .entryId(entry.getId())
                .totalHours(timesheet.getTotalHours())
                .status(timesheet.getStatus())
                .build();

        context.setResponse(response);
    }
}
