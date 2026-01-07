package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.ApproveTimesheetRequest;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.ApprovalContext;
import com.company.hrms.timesheet.application.service.task.ApproveTimesheetTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForApprovalTask;

import lombok.RequiredArgsConstructor;

@Service("approveTimesheetServiceImpl")
@RequiredArgsConstructor
public class ApproveTimesheetServiceImpl
        implements CommandApiService<ApproveTimesheetRequest, ApproveTimesheetResponse> {

    private final LoadTimesheetForApprovalTask loadTimesheetTask;
    private final ApproveTimesheetTask approveTimesheetTask;

    @Override
    @Transactional
    public ApproveTimesheetResponse execCommand(ApproveTimesheetRequest request, JWTModel currentUser, String... args)
            throws Exception {
        ApprovalContext context = new ApprovalContext(request);

        // 從 Token 設定審核者 ID
        context.setApproverId(UUID.fromString(currentUser.getUserId()));

        BusinessPipeline.start(context)
                .next(loadTimesheetTask)
                .next(approveTimesheetTask)
                .execute();

        return context.getResponse();
    }
}
