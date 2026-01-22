package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.ApproveTaskRequest;
import com.company.hrms.workflow.api.response.ApproveTaskResponse;
import com.company.hrms.workflow.application.service.context.ApproveTaskContext;
import com.company.hrms.workflow.application.service.task.ExecuteApprovalLogicTask;
import com.company.hrms.workflow.application.service.task.LoadWorkflowInstanceTask;
import com.company.hrms.workflow.application.service.task.SaveApproveTask;
import com.company.hrms.workflow.application.service.task.ValidateTaskOwnershipTask;

import lombok.RequiredArgsConstructor;

/**
 * Service: 核准任務
 */
@Service("approveTaskServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ApproveTaskServiceImpl implements CommandApiService<ApproveTaskRequest, ApproveTaskResponse> {

    private final LoadWorkflowInstanceTask loadWorkflowInstanceTask;
    private final ValidateTaskOwnershipTask validateTaskOwnershipTask;
    private final ExecuteApprovalLogicTask executeApprovalLogicTask;
    private final SaveApproveTask saveApproveTask;

    @Override
    public ApproveTaskResponse execCommand(ApproveTaskRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // Ensure request has approverId from token if not set (or validate match)
        // Here we trust the request populated by controller or overrides it
        if (currentUser != null) {
            request.setApproverId(currentUser.getEmployeeNumber());
        }

        ApproveTaskContext context = new ApproveTaskContext(request);

        BusinessPipeline.start(context)
                .next(loadWorkflowInstanceTask)
                .next(validateTaskOwnershipTask)
                .next(executeApprovalLogicTask)
                .next(saveApproveTask)
                .execute();

        return context.getResponse();
    }
}
