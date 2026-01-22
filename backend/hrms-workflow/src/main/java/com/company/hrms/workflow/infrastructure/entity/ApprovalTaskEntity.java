package com.company.hrms.workflow.infrastructure.entity;

import java.time.LocalDateTime;

import com.company.hrms.workflow.domain.model.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow_approval_tasks")
@Getter
@Setter
public class ApprovalTaskEntity {

    @Id
    @Column(name = "task_id")
    private String taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private WorkflowInstanceEntity workflowInstance;

    @Column(name = "node_id")
    private String nodeId;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "assignee_id")
    private String assigneeId;

    @Column(name = "assignee_name")
    private String assigneeName;

    @Column(name = "delegated_to_id")
    private String delegatedToId;

    @Column(name = "delegated_to_name")
    private String delegatedToName;

    @Column(name = "approver_id")
    private String approverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "comments")
    private String comments;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_overdue")
    private boolean isOverdue;
}
