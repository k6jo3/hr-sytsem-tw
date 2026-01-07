package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.RejectionContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RejectTimesheetTask implements PipelineTask<RejectionContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(RejectionContext context) {
        Timesheet timesheet = context.getTimesheet();

        // Use Set Rejector from Context (derived from Current User)
        // Note: Request has reason
        timesheet.reject(context.getRejectorId(), context.getRequest().getReason());

        timesheetRepository.save(timesheet);

        RejectTimesheetResponse response = RejectTimesheetResponse.builder()
                .timesheetId(timesheet.getId().getValue())
                .status(timesheet.getStatus())
                .reason(timesheet.getRejectionReason())
                .build();

        context.setResponse(response);
    }
}
