package com.company.hrms.performance.domain.repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.ICommandRepository;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.IQueryRepository;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;

/**
 * 考核記錄 Repository 介面
 * 
 * 繼承基礎 Repository 介面，提供標準的 CRUD 和查詢操作
 */
public interface IPerformanceReviewRepository
                extends IQueryRepository<PerformanceReview, ReviewId>,
                ICommandRepository<PerformanceReview, ReviewId> {

        // 基礎介面已提供以下方法：
        // - findById(ReviewId id)
        // - save(PerformanceReview entity)
        // - update(PerformanceReview entity)
        // - delete(PerformanceReview entity)
        // - deleteById(ReviewId id)
        // - existsById(ReviewId id)
        // - findPage(QueryGroup group, Pageable pageable)
        // - findPage(Condition<C> condition)
        // - findOne(QueryGroup group)
        // - findAll(QueryGroup group)
        // - count(QueryGroup group)
        // - exists(QueryGroup group)

        // 如需額外的業務查詢方法，可在此處新增
}
