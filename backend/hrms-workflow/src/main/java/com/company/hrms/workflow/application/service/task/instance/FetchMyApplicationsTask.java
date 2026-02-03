package com.company.hrms.workflow.application.service.task.instance;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.application.service.context.MyApplicationsContext;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.company.hrms.workflow.infrastructure.repository.WorkflowInstanceQueryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FetchMyApplicationsTask implements PipelineTask<MyApplicationsContext> {

    private final WorkflowInstanceQueryRepository repository;

    @Override
    public void execute(MyApplicationsContext context) throws Exception {
        // Set applicantId to current user for security
        context.getRequest().setApplicantId(context.getCurrentUser().getUserId());

        QueryGroup group = QueryBuilder.fromCondition(context.getRequest());

        Page<WorkflowInstanceEntity> page = repository.findPage(group, context.getPageable());
        context.setEntities(page);
    }
}
