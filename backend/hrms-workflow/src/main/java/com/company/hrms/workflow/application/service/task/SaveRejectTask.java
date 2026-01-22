package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.RejectTaskContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Task: 儲存流程實例 (for Reject)
 */
@Component
@RequiredArgsConstructor
public class SaveRejectTask implements PipelineTask<RejectTaskContext> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public void execute(RejectTaskContext context) {
        WorkflowInstance instance = context.getInstance();

        instanceRepository.save(instance);

        if (context.getResponse() != null) {
            context.getResponse().setInstanceStatus(instance.getStatus());
        }
    }
}
