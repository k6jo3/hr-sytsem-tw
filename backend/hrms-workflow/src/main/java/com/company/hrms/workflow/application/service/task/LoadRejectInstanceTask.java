package com.company.hrms.workflow.application.service.task;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.RejectTaskContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Task: 載入流程實例 (for Reject)
 */
@Component
@RequiredArgsConstructor
public class LoadRejectInstanceTask implements PipelineTask<RejectTaskContext> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public void execute(RejectTaskContext context) {
        String instanceId = context.getRequest().getInstanceId();

        Optional<WorkflowInstance> opt = instanceRepository.findById(new WorkflowInstanceId(instanceId));

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("找不到流程實例: " + instanceId);
        }

        context.setInstance(opt.get());
    }
}
