package com.company.hrms.workflow.application.service.task;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.StartWorkflowResponse;
import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Task: 儲存流程實例並建構回應
 */
@Component
@RequiredArgsConstructor
public class SaveWorkflowInstanceTask implements PipelineTask<StartWorkflowContext> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public void execute(StartWorkflowContext context) {
        WorkflowInstance instance = context.getInstance();

        // 保存 Aggregate
        instanceRepository.save(instance);

        // 建構 Response
        StartWorkflowResponse response = StartWorkflowResponse.builder()
                .instanceId(instance.getInstanceId())
                .definitionId(instance.getDefinitionId()) // Assuming definitionId is String field in Aggregate
                .status(instance.getStatus())
                .startedAt(instance.getCreatedAt() != null ? instance.getCreatedAt() : LocalDateTime.now())
                .message("流程已成功發起")
                .build();

        context.setResponse(response);
    }
}
