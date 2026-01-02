package com.company.hrms.timesheet.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.ApprovalContext;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApproveTimesheetTask implements PipelineTask<ApprovalContext> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public void execute(ApprovalContext context) {
        Timesheet timesheet = context.getTimesheet();

        // Use Set Approver from Context (derived from Current User)
        timesheet.approve(context.getApproverId());

        timesheetRepository.save(timesheet);

        ApproveTimesheetResponse response = ApproveTimesheetResponse.builder()
                .timesheetId(timesheet.getId().getValue())
                .status(timesheet.getStatus())
                .approvedBy(timesheet.getApprovedBy())
                .approvedAt(timesheet.getApprovedAt())
                .build();

        context.setResponse(response);
    }
}
