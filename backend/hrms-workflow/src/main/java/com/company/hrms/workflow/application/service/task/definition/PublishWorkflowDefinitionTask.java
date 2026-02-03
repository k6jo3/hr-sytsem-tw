package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.PublishWorkflowDefinitionContext;

@Component
public class PublishWorkflowDefinitionTask implements PipelineTask<PublishWorkflowDefinitionContext> {

    @Override
    public void execute(PublishWorkflowDefinitionContext context) throws Exception {
        context.getDefinition().publish();
        context.getDefinition().setUpdatedBy(context.getCurrentUser().getUserId());
    }
}
