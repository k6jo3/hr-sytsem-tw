package com.company.hrms.timesheet.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
/**
 * 通用讀取 Timesheet 任務
 * 適用於所有需要透過 ID 讀取 Timesheet 的 Context
 * Context 必須實作 HasTimesheetId 介面 (或者我們使用反射/特定 Context 類型)
 * 為簡單起見，我們先針對特定 Context 實作，或者在 Context 中定義通用方法。
 * 由於 Java 泛型限制，我們針對 SubmissionContext 實作，或者建立 BaseTimesheetContext。
 * 
 * 這裡我們先針對 SubmissionContext 實作。
 */
import com.company.hrms.timesheet.application.service.context.SubmissionContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadTimesheetForSubmissionTask implements PipelineTask<SubmissionContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(SubmissionContext context) {
        UUID timesheetId = context.getRequest().getTimesheetId();

        Timesheet timesheet = timesheetRepository.findById(new TimesheetId(timesheetId))
                .orElseThrow(() -> new EntityNotFoundException("Timesheet not found: " + timesheetId));

        context.setTimesheet(timesheet);
    }
}
