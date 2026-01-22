package com.company.hrms.workflow.application.service.context;

import com.company.hrms.workflow.api.request.CreateDelegationRequest;
import com.company.hrms.workflow.api.response.CreateDelegationResponse;
import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDelegationContext extends WorkflowContext {
    // Input
    private CreateDelegationRequest request;
    private String delegatorId; // Current user

    // Intermediate
    private UserDelegation userDelegation;

    // Output
    private CreateDelegationResponse response;

    public UserDelegationContext(CreateDelegationRequest request, String delegatorId) {
        this.request = request;
        this.delegatorId = delegatorId;
    }
}
