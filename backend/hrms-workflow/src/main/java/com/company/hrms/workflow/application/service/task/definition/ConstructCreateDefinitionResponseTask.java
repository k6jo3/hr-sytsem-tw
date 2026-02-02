package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.CreateWorkflowDefinitionResponse;
import com.company.hrms.workflow.application.service.context.CreateWorkflowDefinitionContext;

@Component
public class ConstructCreateDefinitionResponseTask implements PipelineTask<CreateWorkflowDefinitionContext> {

    @Override
    public void execute(CreateWorkflowDefinitionContext context) throws Exception {
        context.setResponse(new CreateWorkflowDefinitionResponse(context.getDefinition().getDefinitionId()));
    }
}
