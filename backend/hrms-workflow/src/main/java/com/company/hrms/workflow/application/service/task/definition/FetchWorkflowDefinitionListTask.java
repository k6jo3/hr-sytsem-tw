package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.application.service.context.GetWorkflowDefinitionListContext;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.company.hrms.workflow.infrastructure.repository.WorkflowDefinitionQueryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FetchWorkflowDefinitionListTask implements PipelineTask<GetWorkflowDefinitionListContext> {

    private final WorkflowDefinitionQueryRepository repository;

    @Override
    public void execute(GetWorkflowDefinitionListContext context) throws Exception {
        QueryGroup group = QueryBuilder.fromCondition(context.getRequest());
        Page<WorkflowDefinitionEntity> page = repository.findPage(group, context.getPageable());
        context.setEntities(page);
    }
}
