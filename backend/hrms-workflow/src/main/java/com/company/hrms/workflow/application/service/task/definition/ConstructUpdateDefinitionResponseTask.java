package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.UpdateWorkflowDefinitionResponse;
import com.company.hrms.workflow.application.service.context.UpdateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;

@Component
public class ConstructUpdateDefinitionResponseTask implements PipelineTask<UpdateWorkflowDefinitionContext> {

    @Override
    public void execute(UpdateWorkflowDefinitionContext context) throws Exception {
        WorkflowDefinition definition = context.getDefinition();

        UpdateWorkflowDefinitionResponse response = new UpdateWorkflowDefinitionResponse();
        response.setDefinitionId(definition.getDefinitionId());
        response.setFlowName(definition.getFlowName());
        response.setFlowType(definition.getFlowType() != null ? definition.getFlowType().name() : null);
        response.setVersion(definition.getVersion());
        response.setUpdatedAt(java.time.LocalDateTime.now());

        context.setResponse(response);
    }
}
