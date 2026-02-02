package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.application.assembler.WorkflowDefinitionAssembler;
import com.company.hrms.workflow.application.service.context.GetWorkflowDefinitionListContext;

@Component
public class TransformWorkflowDefinitionListResponseTask implements PipelineTask<GetWorkflowDefinitionListContext> {

    @Override
    public void execute(GetWorkflowDefinitionListContext context) throws Exception {
        Page<WorkflowDefinitionResponse> responsePage = context.getEntities()
                .map(WorkflowDefinitionAssembler::toResponse);
        context.setResponse(responsePage);
    }
}
