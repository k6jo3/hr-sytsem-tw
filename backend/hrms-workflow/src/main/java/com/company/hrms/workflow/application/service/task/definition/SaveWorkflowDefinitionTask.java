package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.UpdateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveWorkflowDefinitionTask implements PipelineTask<UpdateWorkflowDefinitionContext> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public void execute(UpdateWorkflowDefinitionContext context) throws Exception {
        definitionRepository.save(context.getDefinition());
    }
}
