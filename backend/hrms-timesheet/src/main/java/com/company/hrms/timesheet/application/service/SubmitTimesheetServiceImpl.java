package com.company.hrms.timesheet.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.SubmitTimesheetRequest;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;
import com.company.hrms.timesheet.application.service.context.SubmissionContext;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForSubmissionTask;
import com.company.hrms.timesheet.application.service.task.SubmitTimesheetTask;

import lombok.RequiredArgsConstructor;

@Service("submitTimesheetServiceImpl")
@RequiredArgsConstructor
public class SubmitTimesheetServiceImpl implements CommandApiService<SubmitTimesheetRequest, SubmitTimesheetResponse> {

    private final LoadTimesheetForSubmissionTask loadTimesheetTask;
    private final SubmitTimesheetTask submitTimesheetTask;

    @Override
    @Transactional
    public SubmitTimesheetResponse execCommand(SubmitTimesheetRequest request, JWTModel currentUser, String... args)
            throws Exception {
        SubmissionContext context = new SubmissionContext(request);

        BusinessPipeline.start(context)
                .next(loadTimesheetTask)
                .next(submitTimesheetTask)
                .execute();

        return context.getResponse();
    }
}
