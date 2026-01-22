package com.company.hrms.workflow.application.service.task;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.RejectTaskResponse;
import com.company.hrms.workflow.application.service.context.RejectTaskContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;

/**
 * Task: 執行駁回邏輯
 */
@Component
public class ExecuteRejectionLogicTask implements PipelineTask<RejectTaskContext> {

    @Override
    public void execute(RejectTaskContext context) {
        WorkflowInstance instance = context.getInstance();
        var request = context.getRequest();

        instance.rejectTask(
                request.getTaskId(),
                request.getApproverId(),
                request.getReason());

        RejectTaskResponse response = RejectTaskResponse.builder()
                .instanceId(instance.getInstanceId())
                .taskId(request.getTaskId())
                .status(TaskStatus.REJECTED)
                .instanceStatus(instance.getStatus())
                .completedAt(LocalDateTime.now())
                .message("任務已駁回")
                .build();

        context.setResponse(response);
    }
}
