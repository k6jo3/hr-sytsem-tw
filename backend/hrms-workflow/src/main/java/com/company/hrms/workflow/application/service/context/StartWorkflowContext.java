package com.company.hrms.workflow.application.service.context;

import com.company.hrms.workflow.api.request.StartWorkflowRequest;
import com.company.hrms.workflow.api.response.StartWorkflowResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 發起流程 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartWorkflowContext extends WorkflowContext {

    // Request
    private StartWorkflowRequest request;

    // Domain Aggregates
    private WorkflowDefinition definition;
    private WorkflowInstance instance;

    // Response
    private StartWorkflowResponse response;

    public StartWorkflowContext(StartWorkflowRequest request) {
        this.request = request;
    }
}
