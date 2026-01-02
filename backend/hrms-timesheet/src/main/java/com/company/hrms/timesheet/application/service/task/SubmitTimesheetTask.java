package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.SubmissionContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubmitTimesheetTask implements PipelineTask<SubmissionContext> {

    private final ITimesheetRepository timesheetRepository;
    // private final DomainEventPublisher eventPublisher; // If we need to publish
    // explicitly, but Aggregate does registerEvent()

    @Override
    public void execute(SubmissionContext context) {
        Timesheet timesheet = context.getTimesheet();

        // Domain logic: submit() handles validation (not empty) and state change
        timesheet.submit();

        timesheetRepository.save(timesheet);

        SubmitTimesheetResponse response = SubmitTimesheetResponse.builder()
                .timesheetId(timesheet.getId().getValue())
                .status(timesheet.getStatus())
                .submittedAt(timesheet.getSubmittedAt())
                .build();

        context.setResponse(response);
    }
}
