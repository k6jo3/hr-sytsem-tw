package com.company.hrms.workflow.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hrms_wf_delegation")
public class DelegationEntity {
    @Id
    private String delegationId;
    private String applicantId;
    private String delegeeId;
    // Add other fields if needed, simplified stub
}
