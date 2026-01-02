package com.company.hrms.performance.domain.repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.ICommandRepository;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.IQueryRepository;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;

/**
 * 考核週期 Repository 介面
 * 
 * 繼承基礎 Repository 介面，提供標準的 CRUD 和查詢操作
 */
public interface IPerformanceCycleRepository
        extends IQueryRepository<PerformanceCycle, CycleId>,
        ICommandRepository<PerformanceCycle, CycleId> {

    // 基礎介面已提供以下方法：
    // - findById(CycleId id)
    // - save(PerformanceCycle entity)
    // - update(PerformanceCycle entity)
    // - delete(PerformanceCycle entity)
    // - deleteById(CycleId id)
    // - existsById(CycleId id)
    // - findPage(QueryGroup group, Pageable pageable)
    // - findPage(Condition<C> condition)
    // - findOne(QueryGroup group)
    // - findAll(QueryGroup group)
    // - count(QueryGroup group)
    // - exists(QueryGroup group)

    // 如需額外的業務查詢方法，可在此處新增
}
