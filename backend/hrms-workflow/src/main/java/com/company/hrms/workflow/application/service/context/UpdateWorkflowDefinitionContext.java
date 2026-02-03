package com.company.hrms.workflow.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.UpdateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.UpdateWorkflowDefinitionResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkflowDefinitionContext extends PipelineContext {

    // Input
    private final UpdateWorkflowDefinitionRequest request;
    private final String definitionId;
    private final JWTModel currentUser;

    // Internal
    private WorkflowDefinition definition;

    // Output
    private UpdateWorkflowDefinitionResponse response;

    public UpdateWorkflowDefinitionContext(UpdateWorkflowDefinitionRequest request, String definitionId,
            JWTModel currentUser) {
        this.request = request;
        this.definitionId = definitionId;
        this.currentUser = currentUser;
    }
}
