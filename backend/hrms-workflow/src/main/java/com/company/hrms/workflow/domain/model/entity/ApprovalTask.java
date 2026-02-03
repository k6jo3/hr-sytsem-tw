package com.company.hrms.workflow.domain.model.entity;

import java.time.LocalDateTime;

import com.company.hrms.workflow.domain.model.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 審核任務實體 (Aggregate Member)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTask {
    private String taskId;
    private String instanceId; // FK to Aggregate Root
    private String nodeId;
    private String nodeName;

    private String assigneeId; // 應審核人
    private String assigneeName;

    private String delegatedToId; // 代理人 (若有)
    private String delegatedToName;

    private String approverId; // 實際簽核人
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String comment; // 簽核意見

    private LocalDateTime dueDate;
    private boolean isOverdue;

    public void approve(String approverId, String comments) {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("只能核准處理中的任務");
        }
        this.status = TaskStatus.APPROVED;
        this.approverId = approverId;
        this.approvedAt = LocalDateTime.now();
        this.comment = comments;
    }

    public void reject(String approverId, String comments) {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("只能駁回處理中的任務");
        }
        this.status = TaskStatus.REJECTED;
        this.approverId = approverId;
        this.approvedAt = LocalDateTime.now();
        this.comment = comments;
    }

    public void delegate(String delegateToId, String delegateToName, String operatorId) {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("只能轉交處理中的任務");
        }
        if (!this.assigneeId.equals(operatorId)) {
            // 如果當前有代理人，則代理人也可以轉交？
            // 按需求「只有任務負責人可以轉交」
            throw new SecurityException("只有任務負責人可以轉交任務");
        }

        this.status = TaskStatus.DELEGATED;
        this.delegatedToId = delegateToId;
        this.delegatedToName = delegateToName;
        // 在實際轉交邏輯中，可能需要建立一個新的 Task 給被轉交人
        // 這裡僅更新當前 Task 狀態為已轉交
    }
}
