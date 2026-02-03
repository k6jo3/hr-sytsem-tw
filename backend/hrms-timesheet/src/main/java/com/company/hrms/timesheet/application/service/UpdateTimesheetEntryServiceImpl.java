package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.UpdateTimesheetEntryRequest;
import com.company.hrms.timesheet.api.response.UpdateTimesheetEntryResponse;
import com.company.hrms.timesheet.application.service.context.UpdateTimesheetEntryContext;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetByIdTask;
import com.company.hrms.timesheet.application.service.task.UpdateEntryTask;

import lombok.RequiredArgsConstructor;

@Service("updateTimesheetEntryServiceImpl")
@RequiredArgsConstructor
public class UpdateTimesheetEntryServiceImpl
        implements CommandApiService<UpdateTimesheetEntryRequest, UpdateTimesheetEntryResponse> {

    private final LoadTimesheetByIdTask loadTimesheetByIdTask;
    private final UpdateEntryTask updateEntryTask;

    @Override
    @Transactional
    public UpdateTimesheetEntryResponse execCommand(UpdateTimesheetEntryRequest request, JWTModel currentUser,
            String... args)
            throws Exception {
        UpdateTimesheetEntryContext context = new UpdateTimesheetEntryContext(request);
        context.setUserId(UUID.fromString(currentUser.getUserId()));
        BusinessPipeline.start(context)
                .next(loadTimesheetByIdTask)
                .next(updateEntryTask)
                .execute();

        return context.getResponse();
    }
}
