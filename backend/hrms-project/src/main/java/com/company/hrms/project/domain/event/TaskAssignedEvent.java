package com.company.hrms.project.domain.event;

import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskAssignedEvent extends DomainEvent {
    private String taskId;
    private UUID projectId;
    private UUID assigneeId;
    private String taskName;

    @Override
    public String getAggregateType() {
        return "Task";
    }

    @Override
    public String getAggregateId() {
        return taskId;
    }
}
