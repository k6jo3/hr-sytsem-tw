package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.CreateDelegationRequest;
import com.company.hrms.workflow.api.response.CreateDelegationResponse;
import com.company.hrms.workflow.application.service.context.UserDelegationContext;
import com.company.hrms.workflow.application.service.task.SaveDelegationTask;
import com.company.hrms.workflow.application.service.task.ValidateAndCreateDelegationTask;

import lombok.RequiredArgsConstructor;

@Service("createDelegationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateDelegationServiceImpl
        implements CommandApiService<CreateDelegationRequest, CreateDelegationResponse> {

    private final ValidateAndCreateDelegationTask validateAndCreateTask;
    private final SaveDelegationTask saveTask;

    @Override
    public CreateDelegationResponse execCommand(CreateDelegationRequest request, JWTModel currentUser, String... args)
            throws Exception {

        UserDelegationContext ctx = new UserDelegationContext(request, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(validateAndCreateTask)
                .next(saveTask)
                .execute();

        return ctx.getResponse();
    }
}
