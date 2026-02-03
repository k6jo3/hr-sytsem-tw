package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.PublishWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadWorkflowDefinitionForPublishTask implements PipelineTask<PublishWorkflowDefinitionContext> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public void execute(PublishWorkflowDefinitionContext context) throws Exception {
        String id = context.getDefinitionId();
        WorkflowDefinition definition = definitionRepository.findById(new WorkflowDefinitionId(id))
                .orElseThrow(() -> new java.util.NoSuchElementException("Workflow Definition not found: " + id));
        context.setDefinition(definition);
    }
}
