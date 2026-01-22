package com.company.hrms.workflow.application.service.context;

import com.company.hrms.workflow.api.request.RejectTaskRequest;
import com.company.hrms.workflow.api.response.RejectTaskResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 駁回任務 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RejectTaskContext extends WorkflowContext {

    private RejectTaskRequest request;

    private WorkflowInstance instance;

    // 當前處理的任務實體
    private ApprovalTask currentTask;

    private RejectTaskResponse response;

    public RejectTaskContext(RejectTaskRequest request) {
        this.request = request;
    }
}
