package com.company.hrms.timesheet.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.application.service.context.TimesheetEntryContext;
import com.company.hrms.timesheet.application.service.task.GetOrCreateTimesheetTask;
import com.company.hrms.timesheet.application.service.task.SaveEntryTask;
import com.company.hrms.timesheet.application.service.task.ValidateEntryTask;

import lombok.RequiredArgsConstructor;

@Service("createEntryServiceImpl")
@RequiredArgsConstructor
public class CreateEntryServiceImpl implements CommandApiService<CreateEntryRequest, CreateEntryResponse> {

    private final GetOrCreateTimesheetTask getOrCreateTimesheetTask;
    private final ValidateEntryTask validateEntryTask;
    private final SaveEntryTask saveEntryTask;

    @Override
    @Transactional
    public CreateEntryResponse execCommand(CreateEntryRequest request, JWTModel currentUser, String... args)
            throws Exception {
        TimesheetEntryContext context = new TimesheetEntryContext(request);

        BusinessPipeline.start(context)
                .next(getOrCreateTimesheetTask)
                .next(validateEntryTask)
                .next(saveEntryTask)
                .execute();

        return context.getResponse();
    }
}
