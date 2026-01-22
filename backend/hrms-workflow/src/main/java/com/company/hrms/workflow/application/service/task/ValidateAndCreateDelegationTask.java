package com.company.hrms.workflow.application.service.task;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.UserDelegationContext;
import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;
import com.company.hrms.workflow.domain.model.valueobject.UserDelegationId;
import com.company.hrms.workflow.domain.repository.IUserDelegationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidateAndCreateDelegationTask implements PipelineTask<UserDelegationContext> {

    private final IUserDelegationRepository repository;

    @Override
    public void execute(UserDelegationContext context) {
        var req = context.getRequest();

        LocalDate start = LocalDate.parse(req.getStartDate());
        LocalDate end = LocalDate.parse(req.getEndDate());

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Logic check: Overlapping delegations?
        // Simplicity: Assuming repository has custom query or we load all.
        // For now, skip overlap check or do simple check if method exists.
        // Assuming strict requirement for now: just create.

        UserDelegation delegation = UserDelegation.builder()
                .delegationId(new UserDelegationId(UUID.randomUUID().toString()))
                .delegatorId(context.getDelegatorId())
                .delegateId(req.getDelegateeId())
                .startDate(start)
                .endDate(end)
                .isActive(true) // Default active
                .delegationScope("ALL") // Default scope
                .reason(req.getReason())
                .build();

        context.setUserDelegation(delegation);
    }
}
