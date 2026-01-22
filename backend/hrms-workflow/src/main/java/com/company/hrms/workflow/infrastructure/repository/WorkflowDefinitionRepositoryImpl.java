package com.company.hrms.workflow.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;
import com.company.hrms.workflow.infrastructure.entity.QWorkflowDefinitionEntity;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WorkflowDefinitionRepositoryImpl
        extends CommandBaseRepository<WorkflowDefinitionEntity, String>
        implements IWorkflowDefinitionRepository {

    private final ObjectMapper objectMapper;

    public WorkflowDefinitionRepositoryImpl(JPAQueryFactory factory, ObjectMapper objectMapper) {
        super(factory, WorkflowDefinitionEntity.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<WorkflowDefinition> findLatestActive(FlowType flowType) {
        QWorkflowDefinitionEntity qDef = QWorkflowDefinitionEntity.workflowDefinitionEntity;

        WorkflowDefinitionEntity entity = factory.selectFrom(qDef)
                .where(qDef.flowType.eq(flowType.name())
                        .and(qDef.status.eq(DefinitionStatus.ACTIVE)))
                .orderBy(qDef.version.desc())
                .fetchFirst();

        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public boolean existsByFlowType(FlowType flowType) {
        QWorkflowDefinitionEntity qDef = QWorkflowDefinitionEntity.workflowDefinitionEntity;
        return factory.selectFrom(qDef)
                .where(qDef.flowType.eq(flowType.name()))
                .fetchFirst() != null;
    }

    @Override
    public WorkflowDefinition save(WorkflowDefinition domain) {
        WorkflowDefinitionEntity entity = toEntity(domain);

        // Handle new entity versioning or ID generation if needed, but assuming ID is
        // provided or handled by service
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        super.save(entity);
        return domain;
    }

    @Override
    public Optional<WorkflowDefinition> findById(WorkflowDefinitionId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    // === Mappers ===

    private WorkflowDefinitionEntity toEntity(WorkflowDefinition domain) {
        WorkflowDefinitionEntity entity = new WorkflowDefinitionEntity();
        // Here we ideally use default ID if not present OR assume domain has ID.
        // For simplicity, using String directly as WorkflowDefinition currently uses
        // String ID.
        // If I update WorkflowDefinition to use WorkflowDefinitionId, I'd change this
        // to:
        // entity.setDefinitionId(domain.getId().getValue());
        // For now, WorkflowDefinition uses String, so:
        entity.setDefinitionId(domain.getDefinitionId());

        entity.setFlowName(domain.getFlowName());
        entity.setFlowType(domain.getFlowType() != null ? domain.getFlowType().name() : null);
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setVersion(domain.getVersion());
        entity.setDefaultDueDays(domain.getDefaultDueDays());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setPublishedAt(domain.getPublishedAt());

        try {
            if (domain.getNodes() != null) {
                entity.setNodesJson(objectMapper.writeValueAsString(domain.getNodes()));
            }
            if (domain.getEdges() != null) {
                entity.setEdgesJson(objectMapper.writeValueAsString(domain.getEdges()));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing definition nodes/edges", e);
        }

        return entity;
    }

    private WorkflowDefinition toDomain(WorkflowDefinitionEntity entity) {
        List<WorkflowNode> nodes = new ArrayList<>();
        List<WorkflowEdge> edges = new ArrayList<>();

        try {
            if (entity.getNodesJson() != null) {
                nodes = objectMapper.readValue(entity.getNodesJson(), new TypeReference<List<WorkflowNode>>() {
                });
            }
            if (entity.getEdgesJson() != null) {
                edges = objectMapper.readValue(entity.getEdgesJson(), new TypeReference<List<WorkflowEdge>>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing definition nodes/edges", e);
        }

        WorkflowDefinition domain = new WorkflowDefinition(new WorkflowDefinitionId(entity.getDefinitionId()));
        domain.setFlowName(entity.getFlowName());
        domain.setFlowType(entity.getFlowType() != null ? FlowType.valueOf(entity.getFlowType()) : null);
        domain.setDescription(entity.getDescription());
        domain.setNodes(nodes);
        domain.setEdges(edges);
        domain.setStatus(entity.getStatus());
        domain.setVersion(entity.getVersion());
        domain.setDefaultDueDays(entity.getDefaultDueDays());

        domain.setRehydratedFields(entity.getCreatedAt(), entity.getUpdatedAt(), entity.getCreatedBy(),
                entity.getUpdatedBy(), entity.getPublishedAt());

        return domain;
    }
}
