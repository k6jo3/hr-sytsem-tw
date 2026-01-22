package com.company.hrms.workflow.infrastructure.entity;

import java.time.LocalDateTime;

import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow_definitions")
@Getter
@Setter
public class WorkflowDefinitionEntity {

    @Id
    @Column(name = "definition_id")
    private String definitionId;

    @Column(name = "flow_name")
    private String flowName;

    @Column(name = "flow_type")
    private String flowType;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DefinitionStatus status;

    @Column(name = "default_due_days")
    private Integer defaultDueDays;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Lob
    @Column(name = "nodes_json", columnDefinition = "text")
    private String nodesJson;

    @Lob
    @Column(name = "edges_json", columnDefinition = "text")
    private String edgesJson;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
