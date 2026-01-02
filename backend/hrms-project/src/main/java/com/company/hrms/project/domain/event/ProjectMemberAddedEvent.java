package com.company.hrms.project.domain.event;

import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberAddedEvent extends DomainEvent {
    private String projectId;
    private UUID employeeId;
    private String role;

    @Override
    public String getAggregateType() {
        return "Project";
    }

    @Override
    public String getAggregateId() {
        return projectId;
    }
}
