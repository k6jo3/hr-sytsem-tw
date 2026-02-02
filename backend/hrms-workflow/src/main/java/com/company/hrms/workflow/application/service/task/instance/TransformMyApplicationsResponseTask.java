package com.company.hrms.workflow.application.service.task.instance;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.application.assembler.WorkflowInstanceAssembler;
import com.company.hrms.workflow.application.service.context.MyApplicationsContext;

@Component
public class TransformMyApplicationsResponseTask implements PipelineTask<MyApplicationsContext> {

    @Override
    public void execute(MyApplicationsContext context) throws Exception {
        Page<MyApplicationsResponse> responsePage = context.getEntities()
                .map(WorkflowInstanceAssembler::toMyApplicationsResponse);
        context.setResponse(responsePage);
    }
}
