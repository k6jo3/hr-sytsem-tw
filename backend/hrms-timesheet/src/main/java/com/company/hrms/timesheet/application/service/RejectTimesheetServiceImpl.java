package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.RejectTimesheetRequest;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.RejectionContext;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForRejectionTask;
import com.company.hrms.timesheet.application.service.task.RejectTimesheetTask;

import lombok.RequiredArgsConstructor;

@Service("rejectTimesheetServiceImpl")
@RequiredArgsConstructor
public class RejectTimesheetServiceImpl implements CommandApiService<RejectTimesheetRequest, RejectTimesheetResponse> {

    private final LoadTimesheetForRejectionTask loadTimesheetTask;
    private final RejectTimesheetTask rejectTimesheetTask;

    @Override
    @Transactional
    public RejectTimesheetResponse execCommand(RejectTimesheetRequest request, JWTModel currentUser, String... args)
            throws Exception {
        RejectionContext context = new RejectionContext(request);

        // 從 Token 設定駁回者 ID
        context.setRejectorId(UUID.fromString(currentUser.getUserId()));

        BusinessPipeline.start(context)
                .next(loadTimesheetTask)
                .next(rejectTimesheetTask)
                .execute();

        return context.getResponse();
    }
}
