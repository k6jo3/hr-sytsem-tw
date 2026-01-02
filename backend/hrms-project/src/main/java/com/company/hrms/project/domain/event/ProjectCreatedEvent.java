package com.company.hrms.project.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCreatedEvent extends DomainEvent {
    private String projectId;
    private String projectCode;
    private String projectName;

    @Override
    public String getAggregateType() {
        return "Project";
    }

    @Override
    public String getAggregateId() {
        return projectId;
    }
}
