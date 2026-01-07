package com.company.hrms.timesheet.application.service.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.application.service.context.TimesheetEntryContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetOrCreateTimesheetTask implements PipelineTask<TimesheetEntryContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(TimesheetEntryContext context) {
        LocalDate workDate = context.getRequest().getWorkDate();
        LocalDate weekStart = workDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Optional<Timesheet> existingTimesheet = timesheetRepository.findByEmployeeAndWeek(
                context.getRequest().getEmployeeId(),
                weekStart);

        Timesheet timesheet = existingTimesheet
                .orElseGet(() -> Timesheet.create(context.getRequest().getEmployeeId(), weekStart));

        context.setTimesheet(timesheet);
    }
}
