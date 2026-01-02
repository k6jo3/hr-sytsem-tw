package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.response.DeleteTimesheetEntryResponse;
import com.company.hrms.timesheet.application.service.context.DeleteTimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeleteEntryTask implements PipelineTask<DeleteTimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(DeleteTimesheetEntryContext context) {
        Timesheet timesheet = context.getTimesheet();
        var request = context.getRequest();

        timesheet.removeEntry(request.getEntryId());

        timesheetRepository.save(timesheet);

        context.setResponse(DeleteTimesheetEntryResponse.builder()
                .success(true)
                .build());
    }
}
