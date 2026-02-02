package com.company.hrms.workflow.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.PublishWorkflowDefinitionRequest;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublishWorkflowDefinitionContext extends PipelineContext {

    private final PublishWorkflowDefinitionRequest request;
    private final String definitionId;
    private final JWTModel currentUser;

    private WorkflowDefinition definition;

    public PublishWorkflowDefinitionContext(PublishWorkflowDefinitionRequest request, String definitionId,
            JWTModel currentUser) {
        this.request = request;
        this.definitionId = definitionId;
        this.currentUser = currentUser;
    }
}
