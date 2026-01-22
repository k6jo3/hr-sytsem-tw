package com.company.hrms.workflow.application.service.context;

import com.company.hrms.workflow.api.request.ApproveTaskRequest;
import com.company.hrms.workflow.api.response.ApproveTaskResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 核准任務 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApproveTaskContext extends WorkflowContext {

    private ApproveTaskRequest request;

    private WorkflowInstance instance;

    // 當前處理的任務實體
    private ApprovalTask currentTask;

    private ApproveTaskResponse response;

    public ApproveTaskContext(ApproveTaskRequest request) {
        this.request = request;
    }
}
