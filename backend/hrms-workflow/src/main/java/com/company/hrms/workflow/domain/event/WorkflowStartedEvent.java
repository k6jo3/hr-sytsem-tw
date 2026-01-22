package com.company.hrms.workflow.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.workflow.domain.model.enums.FlowType;

import lombok.Getter;

/**
 * 流程啟動事件
 */
@Getter
public class WorkflowStartedEvent extends DomainEvent {

    private final String instanceId;
    private final String definitionId;
    private final FlowType flowType;
    private final String businessId;
    private final String businessType;
    private final String applicantId;
    private final LocalDateTime startedAt;

    public WorkflowStartedEvent(
            String instanceId,
            String definitionId,
            FlowType flowType,
            String businessId,
            String businessType,
            String applicantId,
            LocalDateTime startedAt) {
        super();
        this.instanceId = instanceId;
        this.definitionId = definitionId;
        this.flowType = flowType;
        this.businessId = businessId;
        this.businessType = businessType;
        this.applicantId = applicantId;
        this.startedAt = startedAt;
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
