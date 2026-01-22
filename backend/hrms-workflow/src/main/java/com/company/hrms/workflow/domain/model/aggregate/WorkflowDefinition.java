package com.company.hrms.workflow.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.List;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程定義聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowDefinition extends AggregateRoot<WorkflowDefinitionId> {

    // definitionId managed by AggregateRoot

    private String flowName;
    private FlowType flowType;
    private String description;

    private List<WorkflowNode> nodes;
    private List<WorkflowEdge> edges;

    private DefinitionStatus status;
    private Integer version;
    private Integer defaultDueDays;

    private String createdBy;
    private String updatedBy;
    private LocalDateTime publishedAt;

    public WorkflowDefinition(WorkflowDefinitionId id) {
        super(id);
    }

    @Builder
    public WorkflowDefinition(WorkflowDefinitionId id, String flowName, FlowType flowType, String description,
            List<WorkflowNode> nodes, List<WorkflowEdge> edges, DefinitionStatus status, Integer version,
            Integer defaultDueDays, String createdBy, String updatedBy, LocalDateTime publishedAt,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id);
        this.flowName = flowName;
        this.flowType = flowType;
        this.description = description;
        this.nodes = nodes;
        this.edges = edges;
        this.status = status;
        this.version = version;
        this.defaultDueDays = defaultDueDays;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.publishedAt = publishedAt;
        if (createdAt != null)
            this.createdAt = createdAt;
        if (updatedAt != null)
            this.updatedAt = updatedAt;
    }

    // Helper
    public String getDefinitionId() {
        return this.getId() != null ? this.getId().getValue() : null;
    }

    public void setRehydratedFields(LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy,
            String updatedBy, LocalDateTime publishedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.publishedAt = publishedAt;
    }

    // Domain Methods can be added here
    public void publish() {
        // Logic to validate graph connectivity could be here or in a Validator service
        this.status = DefinitionStatus.ACTIVE;
        this.publishedAt = LocalDateTime.now();
        this.version = (this.version == null ? 0 : this.version) + 1;
    }

    public void deactivate() {
        this.status = DefinitionStatus.INACTIVE;
    }
}
