package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.ApproveTaskContext;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;

/**
 * Task: 驗證任務權限
 */
@Component
public class ValidateTaskOwnershipTask implements PipelineTask<ApproveTaskContext> {

    @Override
    public void execute(ApproveTaskContext context) {
        var instance = context.getInstance();
        var request = context.getRequest();
        var approverId = request.getApproverId();
        var taskId = request.getTaskId();

        // 1. 查找任務
        ApprovalTask task = instance.getTasks().stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到任務: " + taskId));

        // 2. 驗證狀態
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("任務狀態非待處理 (Status: " + task.getStatus() + ")");
        }

        // 3. 驗證簽核人
        // 簡單邏輯：Assignee 必須匹配
        // 進階邏輯：若 Assignee 為空，檢查 Candidate Groups
        if (task.getAssigneeId() != null && !task.getAssigneeId().equals(approverId)) {
            throw new SecurityException("無權簽核此任務 (Assignee: " + task.getAssigneeId() + ", User: " + approverId + ")");
        }

        context.setCurrentTask(task);
    }
}
