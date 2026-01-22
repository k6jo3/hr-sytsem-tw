package com.company.hrms.workflow.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;

import lombok.Getter;

/**
 * 流程完成事件
 */
@Getter
public class WorkflowCompletedEvent extends DomainEvent {

    private final String instanceId;
    private final String businessId;
    private final String businessType;
    private final InstanceStatus finalStatus;
    private final LocalDateTime completedAt;

    public WorkflowCompletedEvent(
            String instanceId,
            String businessId,
            String businessType,
            InstanceStatus finalStatus,
            LocalDateTime completedAt) {
        super();
        this.instanceId = instanceId;
        this.businessId = businessId;
        this.businessType = businessType;
        this.finalStatus = finalStatus;
        this.completedAt = completedAt;
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
