package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.CreateDelegationResponse;
import com.company.hrms.workflow.application.service.context.UserDelegationContext;
import com.company.hrms.workflow.domain.repository.IUserDelegationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveDelegationTask implements PipelineTask<UserDelegationContext> {

    private final IUserDelegationRepository repository;

    @Override
    public void execute(UserDelegationContext context) {
        if (context.getUserDelegation() == null) {
            throw new IllegalStateException("UserDelegation not created in context");
        }

        repository.save(context.getUserDelegation());

        context.setResponse(CreateDelegationResponse.builder()
                .delegationId(context.getUserDelegation().getDelegationId())
                .status(context.getUserDelegation().isActive() ? "ACTIVE" : "INACTIVE")
                .build());
    }
}
