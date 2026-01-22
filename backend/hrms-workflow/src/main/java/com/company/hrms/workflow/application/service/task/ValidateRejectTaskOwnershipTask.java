package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.RejectTaskContext;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;

/**
 * Task: 驗證駁回任務權限
 */
@Component
public class ValidateRejectTaskOwnershipTask implements PipelineTask<RejectTaskContext> {

    @Override
    public void execute(RejectTaskContext context) {
        var instance = context.getInstance();
        var request = context.getRequest();
        var approverId = request.getApproverId();
        var taskId = request.getTaskId();

        ApprovalTask task = instance.getTasks().stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到任務: " + taskId));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("任務狀態非待處理 (Status: " + task.getStatus() + ")");
        }

        if (task.getAssigneeId() != null && !task.getAssigneeId().equals(approverId)) {
            throw new SecurityException("無權駁回此任務 (Assignee: " + task.getAssigneeId() + ", User: " + approverId + ")");
        }

        context.setCurrentTask(task);
    }
}
