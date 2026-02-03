package com.company.hrms.workflow.application.service.context;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.GetMyApplicationsRequest;
import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyApplicationsContext extends PipelineContext {

    private final GetMyApplicationsRequest request;
    private final JWTModel currentUser;
    private final Pageable pageable;

    private Page<WorkflowInstanceEntity> entities;
    private Page<MyApplicationsResponse> response;

    public MyApplicationsContext(GetMyApplicationsRequest request, JWTModel currentUser, Pageable pageable) {
        this.request = request;
        this.currentUser = currentUser;
        this.pageable = pageable;
    }
}
