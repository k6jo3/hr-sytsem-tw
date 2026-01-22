package com.company.hrms.workflow.application.service.task;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.ApproveTaskResponse;
import com.company.hrms.workflow.application.service.context.ApproveTaskContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;

/**
 * Task: 執行核准邏輯 (Domain Logic)
 */
@Component
public class ExecuteApprovalLogicTask implements PipelineTask<ApproveTaskContext> {

    @Override
    public void execute(ApproveTaskContext context) {
        WorkflowInstance instance = context.getInstance();
        var request = context.getRequest();

        // 呼叫 Domain Method 完成任務
        // 此方法應負責更新任務狀態、觸發後續節點 (Token 移動)
        instance.approveTask(
                request.getTaskId(),
                request.getApproverId(),
                request.getComment(),
                request.getVariables());

        // 建構 Response
        // 注意：SaveWorkflowInstanceTask 會負責實際存檔
        ApproveTaskResponse response = ApproveTaskResponse.builder()
                .instanceId(instance.getInstanceId())
                .taskId(request.getTaskId())
                .status(com.company.hrms.workflow.domain.model.enums.TaskStatus.APPROVED)
                .instanceStatus(instance.getStatus())
                .completedAt(LocalDateTime.now())
                .message("任務已核准")
                .build();

        context.setResponse(response);
    }
}
