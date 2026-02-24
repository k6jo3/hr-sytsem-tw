package com.company.hrms.workflow.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;
import com.company.hrms.workflow.infrastructure.entity.ApprovalTaskEntity;
import com.company.hrms.workflow.infrastructure.entity.QWorkflowInstanceEntity;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository

public class WorkflowInstanceRepositoryImpl
        extends CommandBaseRepository<WorkflowInstanceEntity, String>
        implements IWorkflowInstanceRepository {

    private final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;

    public WorkflowInstanceRepositoryImpl(
            JPAQueryFactory factory,
            ObjectMapper objectMapper,
            EventPublisher eventPublisher) {
        super(factory, WorkflowInstanceEntity.class);
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean existsByBusinessIdAndType(String businessId, String businessType) {
        QWorkflowInstanceEntity qInst = QWorkflowInstanceEntity.workflowInstanceEntity;
        return factory.selectFrom(qInst)
                .where(qInst.businessId.eq(businessId)
                        .and(qInst.businessType.eq(businessType)))
                .fetchFirst() != null;
    }

    @Override
    public WorkflowInstance save(WorkflowInstance domain) {
        WorkflowInstanceEntity entity = toEntity(domain);

        // Ensure bidirectional relationship for tasks
        if (entity.getTasks() != null) {
            entity.getTasks().forEach(t -> t.setWorkflowInstance(entity));
        }

        super.save(entity);

        // Publish Events
        domain.getDomainEvents().forEach(eventPublisher::publish);
        domain.clearDomainEvents();

        return domain;
    }

    @Override
    public Optional<WorkflowInstance> findById(WorkflowInstanceId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public void saveAll(List<WorkflowInstance> instances) {
        if (instances == null)
            return;
        instances.forEach(this::save);
    }

    // === Mappers ===

    private WorkflowInstanceEntity toEntity(WorkflowInstance domain) {
        WorkflowInstanceEntity entity = new WorkflowInstanceEntity();
        entity.setInstanceId(domain.getId().getValue());
        entity.setDefinitionId(domain.getDefinitionId());
        entity.setFlowType(domain.getFlowType());
        entity.setBusinessType(domain.getBusinessType());
        entity.setBusinessId(domain.getBusinessId());
        entity.setBusinessUrl(domain.getBusinessUrl());
        entity.setApplicantId(domain.getApplicantId());
        entity.setApplicantName(domain.getApplicantName());
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setDepartmentName(domain.getDepartmentName());
        entity.setSummary(domain.getSummary());
        entity.setStatus(domain.getStatus());
        entity.setCurrentNodeId(domain.getCurrentNodeId());
        entity.setCurrentNodeName(domain.getCurrentNodeName());
        entity.setStartedAt(domain.getStartedAt());
        entity.setCompletedAt(domain.getCompletedAt());

        try {
            if (domain.getVariables() != null) {
                entity.setVariablesJson(objectMapper.writeValueAsString(domain.getVariables()));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing instance variables", e);
        }

        if (domain.getTasks() != null) {
            List<ApprovalTaskEntity> taskEntities = domain.getTasks().stream()
                    .map(this::toTaskEntity)
                    .collect(Collectors.toList());
            entity.setTasks(taskEntities);
        }

        return entity;
    }

    private ApprovalTaskEntity toTaskEntity(ApprovalTask domain) {
        ApprovalTaskEntity entity = new ApprovalTaskEntity();
        entity.setTaskId(domain.getTaskId());
        entity.setNodeId(domain.getNodeId());
        entity.setNodeName(domain.getNodeName());
        entity.setAssigneeId(domain.getAssigneeId());
        entity.setAssigneeName(domain.getAssigneeName());
        entity.setDelegatedToId(domain.getDelegatedToId());
        entity.setDelegatedToName(domain.getDelegatedToName());
        entity.setApproverId(domain.getApproverId());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setComments(domain.getComment());
        entity.setDueDate(domain.getDueDate());
        entity.setOverdue(domain.isOverdue());
        return entity;
    }

    private WorkflowInstance toDomain(WorkflowInstanceEntity entity) {
        Map<String, Object> variables = null;
        try {
            if (entity.getVariablesJson() != null) {
                variables = objectMapper.readValue(entity.getVariablesJson(), new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing instance variables", e);
        }

        List<ApprovalTask> tasks = new ArrayList<>();
        if (entity.getTasks() != null) {
            tasks = entity.getTasks().stream()
                    .map(this::toTaskDomain)
                    .collect(Collectors.toList());
        }

        WorkflowInstance domain = new WorkflowInstance(new WorkflowInstanceId(entity.getInstanceId()));
        domain.setDefinitionId(entity.getDefinitionId());
        domain.setFlowType(entity.getFlowType());
        domain.setBusinessType(entity.getBusinessType());
        domain.setBusinessId(entity.getBusinessId());
        domain.setBusinessUrl(entity.getBusinessUrl());
        domain.setApplicantId(entity.getApplicantId());
        domain.setApplicantName(entity.getApplicantName());
        domain.setDepartmentId(entity.getDepartmentId());
        domain.setDepartmentName(entity.getDepartmentName());
        domain.setSummary(entity.getSummary());
        domain.setVariables(variables);
        domain.setStatus(entity.getStatus());
        domain.setCurrentNodeId(entity.getCurrentNodeId());
        domain.setCurrentNodeName(entity.getCurrentNodeName());
        domain.setStartedAt(entity.getStartedAt());
        domain.setCompletedAt(entity.getCompletedAt());
        domain.setTasks(tasks);

        return domain;
    }

    private ApprovalTask toTaskDomain(ApprovalTaskEntity entity) {
        ApprovalTask domain = new ApprovalTask();
        domain.setTaskId(entity.getTaskId());
        domain.setInstanceId(entity.getWorkflowInstance().getInstanceId()); // FK
        domain.setNodeId(entity.getNodeId());
        domain.setNodeName(entity.getNodeName());
        domain.setAssigneeId(entity.getAssigneeId());
        domain.setAssigneeName(entity.getAssigneeName());
        domain.setDelegatedToId(entity.getDelegatedToId());
        domain.setDelegatedToName(entity.getDelegatedToName());
        domain.setApproverId(entity.getApproverId());
        domain.setStatus(entity.getStatus());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setApprovedAt(entity.getApprovedAt());
        domain.setComment(entity.getComments());
        domain.setDueDate(entity.getDueDate());
        domain.setOverdue(entity.isOverdue());
        return domain;
    }

    @Override
    public Page<WorkflowInstance> search(
            QueryGroup queryGroup,
            Pageable pageable) {

        // 使用 BaseRepository 的 findPage 方法查詢 Entity
        Page<WorkflowInstanceEntity> entityPage = super.findPage(queryGroup, pageable);

        // 使用明確的 mapper 轉換，避免 objectMapper.convertValue() 在雙向關聯時產生無限遞迴
        // TODO: 考慮使用 toDomainWithoutTasks() 改善 N+1 查詢問題（list 場景不需要載入 tasks）
        List<WorkflowInstance> domainList = entityPage.getContent()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(
                domainList,
                pageable,
                entityPage.getTotalElements());
    }
}
