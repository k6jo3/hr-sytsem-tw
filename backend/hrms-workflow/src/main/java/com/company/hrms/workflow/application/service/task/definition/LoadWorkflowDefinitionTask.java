package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.UpdateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Component("loadWorkflowDefinitionForUpdateTask")
@RequiredArgsConstructor
public class LoadWorkflowDefinitionTask implements PipelineTask<UpdateWorkflowDefinitionContext> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public void execute(UpdateWorkflowDefinitionContext context) throws Exception {
        String definitionId = context.getDefinitionId();

        if (definitionId == null || definitionId.isBlank()) {
            throw new IllegalArgumentException("流程定義ID不可為空");
        }

        WorkflowDefinition definition = definitionRepository
                .findById(new WorkflowDefinitionId(definitionId))
                .orElseThrow(() -> new IllegalArgumentException("流程定義不存在: " + definitionId));

        context.setDefinition(definition);
    }
}
