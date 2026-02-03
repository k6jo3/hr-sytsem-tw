package com.company.hrms.workflow.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.CreateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.CreateWorkflowDefinitionResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkflowDefinitionContext extends PipelineContext {

    private final CreateWorkflowDefinitionRequest request;
    private final JWTModel currentUser;

    private WorkflowDefinition definition;
    private CreateWorkflowDefinitionResponse response;

    public CreateWorkflowDefinitionContext(CreateWorkflowDefinitionRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
