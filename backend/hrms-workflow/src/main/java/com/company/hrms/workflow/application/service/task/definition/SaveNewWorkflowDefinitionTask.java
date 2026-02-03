package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.CreateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveNewWorkflowDefinitionTask implements PipelineTask<CreateWorkflowDefinitionContext> {

    private final IWorkflowDefinitionRepository repository;

    @Override
    public void execute(CreateWorkflowDefinitionContext context) throws Exception {
        repository.save(context.getDefinition());
    }
}
