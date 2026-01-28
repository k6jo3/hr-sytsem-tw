package com.company.hrms.workflow.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.workflow.domain.event.WorkflowCompletedEvent;
import com.company.hrms.workflow.domain.event.WorkflowStartedEvent;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程實例聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowInstance extends AggregateRoot<WorkflowInstanceId> {

    // instanceId is managed by AggregateRoot<ID> as 'id'

    private String definitionId;
    private FlowType flowType;

    // 業務單據資訊
    private String businessType;
    private String businessId;
    private String businessUrl;

    // 申請人資訊
    private String applicantId;
    private String applicantName;
    private String departmentId;
    private String departmentName;

    private String summary;
    private Map<String, Object> variables; // 流程變數

    private InstanceStatus status;
    private String currentNodeId;
    private String currentNodeName;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private List<ApprovalTask> tasks = new ArrayList<>();

    public WorkflowInstance(WorkflowInstanceId instanceId) {
        super(instanceId);
    }

    // Helper to get String ID
    public String getInstanceId() {
        return this.getId() != null ? this.getId().getValue() : null;
    }

    public static WorkflowInstance create(
            WorkflowDefinitionId definitionId,
            FlowType flowType,
            String applicantId,
            String businessId,
            String businessType,
            Map<String, Object> variables) {

        WorkflowInstance instance = new WorkflowInstance(
                new WorkflowInstanceId(java.util.UUID.randomUUID().toString()));
        instance.setDefinitionId(definitionId.getValue());
        instance.setFlowType(flowType);
        instance.setApplicantId(applicantId);
        instance.setBusinessId(businessId);
        instance.setBusinessType(businessType);
        instance.setVariables(variables);
        instance.setStatus(InstanceStatus.DRAFT);
        // instance.setCreatedAt(LocalDateTime.now()); // Handled by AggregateRoot
        // constructor
        return instance;
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    // Domain methods use parent registerEvent(DomainEvent)

    // Domain Methods
    public void addTask(ApprovalTask task) {
        this.tasks.add(task);
    }

    public void complete() {
        this.status = InstanceStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        registerEvent(new WorkflowCompletedEvent(
                this.getInstanceId(),
                this.businessId,
                this.businessType,
                this.status,
                this.completedAt));
    }

    public void cancel() {
        this.status = InstanceStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
        // logic to cancel pending tasks
        this.tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .forEach(t -> t.setStatus(TaskStatus.CANCELLED));

        registerEvent(new WorkflowCompletedEvent(
                this.getInstanceId(),
                this.businessId,
                this.businessType,
                this.status,
                this.completedAt));
    }

    public void start() {
        this.startedAt = LocalDateTime.now();
        this.status = InstanceStatus.RUNNING;
        registerEvent(new WorkflowStartedEvent(
                this.getInstanceId(),
                this.definitionId,
                this.flowType,
                this.businessId,
                this.businessType,
                this.applicantId,
                this.startedAt));
    }

    public void approveTask(String taskId, String approverId, String comment, Map<String, Object> variables) {
        ApprovalTask task = this.tasks.stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in PENDING state");
        }

        task.setStatus(TaskStatus.APPROVED);
        task.setApproverId(approverId);
        task.setComment(comment);
        task.setApprovedAt(LocalDateTime.now());

        // Merge variables if needed
        if (variables != null && this.variables != null) {
            this.variables.putAll(variables);
        } else if (variables != null) {
            this.variables = variables;
        }

        // Simple flow logic: Check if all tasks are completed
        // In a real engine, we would move the token here.
        boolean allCompleted = this.tasks.stream()
                .allMatch(t -> t.getStatus() == TaskStatus.APPROVED
                        || t.getStatus() == TaskStatus.REJECTED
                        || t.getStatus() == TaskStatus.CANCELLED);

        if (allCompleted) {
            // For now, if all tasks are done, we assume the workflow is done.
            // Ideally this should depend on the Process Definition graph.
            this.complete();
        }
    }

    public void rejectTask(String taskId, String approverId, String reason) {
        ApprovalTask task = this.tasks.stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in PENDING state");
        }

        task.setStatus(TaskStatus.REJECTED);
        task.setApproverId(approverId);
        task.setComment(reason);
        task.setApprovedAt(LocalDateTime.now()); // Using generic generic 'approvedAt' as completion time, or add
                                                 // rejectedAt

        // 駁回通常意味著流程結束或退回
        // 簡單邏輯：直接 REJECT 整個流程
        this.status = InstanceStatus.REJECTED;
        this.completedAt = LocalDateTime.now();

        // Cancel other pending tasks
        this.tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .forEach(t -> t.setStatus(TaskStatus.CANCELLED));

        // Register Event
        registerEvent(new WorkflowCompletedEvent(
                this.getInstanceId(),
                this.businessId,
                this.businessType,
                this.status,
                this.completedAt));
    }
}
