package com.company.hrms.performance.infrastructure.persistence.querydsl.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;
import com.company.hrms.performance.infrastructure.entity.PerformanceReviewEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 考核記錄 Repository 實作
 * 使用 Fluent-Query-Engine (Querydsl)
 */
@Repository
public class PerformanceReviewRepositoryImpl
        extends BaseRepository<PerformanceReviewEntity, UUID>
        implements IPerformanceReviewRepository {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public PerformanceReviewRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher,
            ObjectMapper objectMapper) {
        super(factory, PerformanceReviewEntity.class);
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public PerformanceReview save(PerformanceReview review) {
        PerformanceReviewEntity entity = toEntity(review);
        super.save(entity);

        // 發布 Domain Events（如果有的話）
        // TODO: 實作 Domain Events 發布

        return review;
    }

    @Override
    public Optional<PerformanceReview> findById(ReviewId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<PerformanceReview> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= Entity 映射 =================

    /**
     * Domain → Entity
     */
    private PerformanceReviewEntity toEntity(PerformanceReview domain) {
        PerformanceReviewEntity entity = new PerformanceReviewEntity();
        entity.setReviewId(domain.getReviewId().getValue());
        entity.setCycleId(domain.getCycleId().getValue());
        entity.setEmployeeId(domain.getEmployeeId());
        entity.setReviewerId(domain.getReviewerId());
        entity.setReviewType(domain.getReviewType());
        entity.setOverallScore(domain.getOverallScore());
        entity.setOverallRating(domain.getOverallRating());
        entity.setFinalScore(domain.getFinalScore());
        entity.setFinalRating(domain.getFinalRating());
        entity.setAdjustmentReason(domain.getAdjustmentReason());
        entity.setComments(domain.getComments());
        entity.setStatus(domain.getStatus());
        entity.setSubmittedAt(domain.getSubmittedAt());
        entity.setFinalizedAt(domain.getFinalizedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // 序列化 EvaluationItems 為 JSON
        if (domain.getEvaluationItems() != null && !domain.getEvaluationItems().isEmpty()) {
            try {
                String itemsJson = objectMapper.writeValueAsString(domain.getEvaluationItems());
                entity.setEvaluationItemsJson(itemsJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("無法序列化評估項目", e);
            }
        }

        return entity;
    }

    /**
     * Entity → Domain
     */
    private PerformanceReview toDomain(PerformanceReviewEntity entity) {
        // 反序列化 EvaluationItems
        List<EvaluationItem> evaluationItems = new ArrayList<>();
        if (entity.getEvaluationItemsJson() != null && !entity.getEvaluationItemsJson().isBlank()) {
            try {
                evaluationItems = objectMapper.readValue(
                        entity.getEvaluationItemsJson(),
                        new TypeReference<List<EvaluationItem>>() {
                        });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("無法反序列化評估項目", e);
            }
        }

        return PerformanceReview.reconstitute(
                ReviewId.of(entity.getReviewId()),
                CycleId.of(entity.getCycleId()),
                entity.getEmployeeId(),
                entity.getReviewerId(),
                entity.getReviewType(),
                evaluationItems,
                entity.getOverallScore(),
                entity.getOverallRating(),
                entity.getFinalScore(),
                entity.getFinalRating(),
                entity.getAdjustmentReason(),
                entity.getComments(),
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getFinalizedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
