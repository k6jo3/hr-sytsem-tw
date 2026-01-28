package com.company.hrms.timesheet.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.timesheet.application.service.context.UpdateTimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;
import com.company.hrms.common.exception.DomainException;

@Component
@RequiredArgsConstructor
public class LoadTimesheetByIdTask implements PipelineTask<UpdateTimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(UpdateTimesheetEntryContext context) {
        UUID timesheetId = context.getRequest().getTimesheetId();

        Timesheet timesheet = timesheetRepository.findById(new TimesheetId(timesheetId))
                .orElseThrow(() -> new EntityNotFoundException("Timesheet", timesheetId.toString()));

        // Ensure user owns this timesheet
        if (!timesheet.getEmployeeId().equals(context.getUserId())) {
            throw new DomainException("無權限修改此工時表");
        }

        context.setTimesheet(timesheet);
    }
}
