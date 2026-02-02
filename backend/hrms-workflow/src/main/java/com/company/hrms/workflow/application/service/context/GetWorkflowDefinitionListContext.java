package com.company.hrms.workflow.application.service.context;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.GetWorkflowDefinitionListRequest;
import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetWorkflowDefinitionListContext extends PipelineContext {

    private final GetWorkflowDefinitionListRequest request;
    private final JWTModel currentUser;
    private final Pageable pageable;

    private Page<WorkflowDefinitionEntity> entities;
    private Page<WorkflowDefinitionResponse> response;

    public GetWorkflowDefinitionListContext(GetWorkflowDefinitionListRequest request, JWTModel currentUser,
            Pageable pageable) {
        this.request = request;
        this.currentUser = currentUser;
        this.pageable = pageable;
    }
}
