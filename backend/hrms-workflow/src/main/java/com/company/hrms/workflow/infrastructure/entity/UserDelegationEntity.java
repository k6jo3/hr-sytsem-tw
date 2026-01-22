package com.company.hrms.workflow.infrastructure.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow_user_delegations")
@Getter
@Setter
public class UserDelegationEntity {

    @Id
    @Column(name = "delegation_id")
    private String delegationId;

    @Column(name = "delegator_id")
    private String delegatorId;

    @Column(name = "delegate_id")
    private String delegateId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "delegation_scope")
    private String delegationScope; // ALL, SPECIFIC

    @Column(name = "specific_flow_types")
    private String specificFlowTypes; // JSON Array of FlowType

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
