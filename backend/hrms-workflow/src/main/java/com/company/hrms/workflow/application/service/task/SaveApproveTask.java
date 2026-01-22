package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.ApproveTaskContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Task: 儲存流程實例 (for Approve)
 */
@Component
@RequiredArgsConstructor
public class SaveApproveTask implements PipelineTask<ApproveTaskContext> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public void execute(ApproveTaskContext context) {
        WorkflowInstance instance = context.getInstance();

        // Save to Repo
        instanceRepository.save(instance);

        // Update Response with latest status if needed
        if (context.getResponse() != null) {
            context.getResponse().setInstanceStatus(instance.getStatus());
        }
    }
}
