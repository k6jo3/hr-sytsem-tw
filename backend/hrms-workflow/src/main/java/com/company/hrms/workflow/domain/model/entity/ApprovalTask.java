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
        this.status = TaskStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.comment = comments;
        // logic to check if approver matches assignee or delegate
    }

    public void reject(String approverId, String comments) {
        this.status = TaskStatus.REJECTED;
        this.approvedAt = LocalDateTime.now(); // or rejectedAt
        this.comment = comments;
    }
}
