package com.company.hrms.performance.infrastructure.persistence.querydsl.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;
import com.company.hrms.performance.infrastructure.entity.PerformanceCycleEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 考核週期 Repository 實作
 * 使用 Fluent-Query-Engine (Querydsl)
 */
@Repository
public class PerformanceCycleRepositoryImpl
        extends CommandBaseRepository<PerformanceCycleEntity, UUID>
        implements IPerformanceCycleRepository {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public PerformanceCycleRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher,
            ObjectMapper objectMapper) {
        super(factory, PerformanceCycleEntity.class);
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public PerformanceCycle save(PerformanceCycle cycle) {
        PerformanceCycleEntity entity = toEntity(cycle);
        super.save(entity);

        // 發布 Domain Events（如果有的話）
        // TODO: 實作 Domain Events 發布

        return cycle;
    }

    @Override
    public void delete(PerformanceCycle cycle) {
        PerformanceCycleEntity entity = toEntity(cycle);
        super.delete(entity);
    }

    @Override
    public Optional<PerformanceCycle> findById(CycleId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<PerformanceCycle> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= Entity 映射 =================

    /**
     * Domain → Entity
     */
    private PerformanceCycleEntity toEntity(PerformanceCycle domain) {
        PerformanceCycleEntity entity = new PerformanceCycleEntity();
        entity.setCycleId(domain.getCycleId().getValue());
        entity.setCycleName(domain.getCycleName());
        entity.setCycleType(domain.getCycleType());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setSelfEvalDeadline(domain.getSelfEvalDeadline());
        entity.setManagerEvalDeadline(domain.getManagerEvalDeadline());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // 序列化 EvaluationTemplate 為 JSON
        if (domain.getTemplate() != null) {
            try {
                String templateJson = objectMapper.writeValueAsString(domain.getTemplate());
                entity.setTemplateJson(templateJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("無法序列化考核表單範本", e);
            }
        }

        return entity;
    }

    /**
     * Entity → Domain
     */
    private PerformanceCycle toDomain(PerformanceCycleEntity entity) {
        // 反序列化 EvaluationTemplate
        EvaluationTemplate template = null;
        if (entity.getTemplateJson() != null && !entity.getTemplateJson().isBlank()) {
            try {
                template = objectMapper.readValue(entity.getTemplateJson(), EvaluationTemplate.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("無法反序列化考核表單範本", e);
            }
        }

        return PerformanceCycle.reconstitute(
                CycleId.of(entity.getCycleId()),
                entity.getCycleName(),
                entity.getCycleType(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getSelfEvalDeadline(),
                entity.getManagerEvalDeadline(),
                entity.getStatus(),
                template,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

}
