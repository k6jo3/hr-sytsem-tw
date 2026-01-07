package com.company.hrms.timesheet.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.timesheet.application.service.context.DeleteTimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadTimesheetForDeleteTask implements PipelineTask<DeleteTimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(DeleteTimesheetEntryContext context) {
        UUID timesheetId = context.getRequest().getTimesheetId();

        Timesheet timesheet = timesheetRepository.findById(new TimesheetId(timesheetId))
                .orElseThrow(() -> new EntityNotFoundException("Timesheet", timesheetId.toString()));

        if (!timesheet.getEmployeeId().equals(context.getUserId())) {
            throw new com.company.hrms.common.exception.DomainException("無權限修改此工時表");
        }

        context.setTimesheet(timesheet);
    }
}
