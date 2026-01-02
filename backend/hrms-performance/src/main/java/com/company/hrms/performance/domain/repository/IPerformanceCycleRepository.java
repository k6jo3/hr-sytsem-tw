package com.company.hrms.performance.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;

/**
 * 考核週期 Repository 介面
 */
public interface IPerformanceCycleRepository {

    /**
     * 儲存考核週期
     */
    PerformanceCycle save(PerformanceCycle cycle);

    /**
     * 根據 ID 查詢
     */
    Optional<PerformanceCycle> findById(CycleId cycleId);

    /**
     * 查詢所有考核週期（分頁）
     */
    Page<PerformanceCycle> findAll(QueryGroup query, Pageable pageable);
}
