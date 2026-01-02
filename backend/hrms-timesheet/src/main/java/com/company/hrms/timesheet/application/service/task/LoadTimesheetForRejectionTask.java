package com.company.hrms.timesheet.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.timesheet.application.service.context.RejectionContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadTimesheetForRejectionTask implements PipelineTask<RejectionContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(RejectionContext context) {
        UUID timesheetId = context.getRequest().getTimesheetId();

        Timesheet timesheet = timesheetRepository.findById(new TimesheetId(timesheetId))
                .orElseThrow(() -> new EntityNotFoundException("Timesheet not found: " + timesheetId));

        context.setTimesheet(timesheet);
    }
}
