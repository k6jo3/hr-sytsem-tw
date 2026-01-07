package com.company.hrms.recruitment.domain.event;

import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 職缺建立事件
 */
public class JobOpeningCreatedEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final OpeningId openingId;
    private final String jobTitle;
    private final UUID departmentId;
    private final String departmentName;
    private final int numberOfPositions;
    private final String salaryRange;
    private final String requirements;
    private final UUID createdBy;
    private final String createdByName;

    private JobOpeningCreatedEvent(
            OpeningId openingId,
            String jobTitle,
            UUID departmentId,
            String departmentName,
            int numberOfPositions,
            String salaryRange,
            String requirements,
            UUID createdBy,
            String createdByName) {
        super();
        this.openingId = openingId;
        this.jobTitle = jobTitle;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.numberOfPositions = numberOfPositions;
        this.salaryRange = salaryRange;
        this.requirements = requirements;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
    }

    public static JobOpeningCreatedEvent create(
            OpeningId openingId,
            String jobTitle,
            UUID departmentId,
            int numberOfPositions) {
        return new JobOpeningCreatedEvent(
                openingId, jobTitle, departmentId, null,
                numberOfPositions, null, null, null, null);
    }

    @Override
    public String getAggregateId() {
        return openingId.getValue().toString();
    }

    @Override
    public String getAggregateType() {
        return "JobOpening";
    }

    // === Getters ===

    public OpeningId getOpeningId() {
        return openingId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getNumberOfPositions() {
        return numberOfPositions;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public String getRequirements() {
        return requirements;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }
}
