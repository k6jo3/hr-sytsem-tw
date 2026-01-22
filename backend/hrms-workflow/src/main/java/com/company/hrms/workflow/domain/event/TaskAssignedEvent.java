package com.company.hrms.workflow.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 任務指派事件
 */
@Getter
public class TaskAssignedEvent extends DomainEvent {

    private final String taskId;
    private final String instanceId;
    private final String assigneeId;
    private final String assigneeName;
    private final String nodeName;
    private final LocalDateTime dueDate;
    private final String summary;

    public TaskAssignedEvent(
            String taskId,
            String instanceId,
            String assigneeId,
            String assigneeName,
            String nodeName,
            LocalDateTime dueDate,
            String summary) {
        super();
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.nodeName = nodeName;
        this.dueDate = dueDate;
        this.summary = summary;
    }

    @Override
    public String getAggregateId() {
        return instanceId;
    }

    @Override
    public String getAggregateType() {
        return "WorkflowInstance";
    }
}
