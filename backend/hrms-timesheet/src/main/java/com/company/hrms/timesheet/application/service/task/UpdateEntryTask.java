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

        // 1. Remove old entry
        timesheet.removeEntry(request.getEntryId());

        // 2. Create new entry (with SAME ID? OR NEW ID? Spec says "Update", but
        // TimesheetEntry probably generates new ID in create factory)
        // If we want to keep ID, we should reconstruct.
        // But `TimesheetEntry.create` generates new ID.
        // Let's create new entry. Standard practice for "Update Value Object" (even if
        // it is Entity in JPA, it acts like VO in Aggregate)

        TimesheetEntry newEntry = TimesheetEntry.create(
                request.getProjectId(),
                request.getTaskId(),
                request.getWorkDate(),
                request.getHours(),
                request.getDescription());

        // 3. Add new entry
        timesheet.addEntry(newEntry);

        // 4. Save
        timesheetRepository.save(timesheet);

        // 5. Set Response
        context.setResponse(UpdateTimesheetEntryResponse.builder()
                .entryId(newEntry.getId().toString())
                .build());
    }
}
