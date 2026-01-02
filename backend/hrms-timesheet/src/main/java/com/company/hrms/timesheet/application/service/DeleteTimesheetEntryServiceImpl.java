package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.DeleteTimesheetEntryRequest;
import com.company.hrms.timesheet.api.response.DeleteTimesheetEntryResponse;
import com.company.hrms.timesheet.application.service.context.DeleteTimesheetEntryContext;
import com.company.hrms.timesheet.application.service.task.DeleteEntryTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForDeleteTask;

import lombok.RequiredArgsConstructor;

@Service("deleteTimesheetEntryServiceImpl")
@RequiredArgsConstructor
public class DeleteTimesheetEntryServiceImpl
        implements CommandApiService<DeleteTimesheetEntryRequest, DeleteTimesheetEntryResponse> {

    private final LoadTimesheetForDeleteTask loadTimesheetTask;
    private final DeleteEntryTask deleteEntryTask;

    @Override
    @Transactional
    public DeleteTimesheetEntryResponse execCommand(DeleteTimesheetEntryRequest request, JWTModel currentUser,
            String... args)
            throws Exception {
        DeleteTimesheetEntryContext context = new DeleteTimesheetEntryContext(request);
        context.setUserId(UUID.fromString(currentUser.getUserId()));

        BusinessPipeline.start(context)
                .next(loadTimesheetTask)
                .next(deleteEntryTask)
                .execute();

        return context.getResponse();
    }
}
