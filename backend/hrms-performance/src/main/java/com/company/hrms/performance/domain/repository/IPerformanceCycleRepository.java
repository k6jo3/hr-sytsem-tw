package com.company.hrms.performance.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

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
    Page<PerformanceCycle> findAll(
            CycleStatus status,
            CycleType cycleType,
            Pageable pageable);

    /**
     * 根據名稱查詢
     */
    Optional<PerformanceCycle> findByName(String cycleName);

    /**
     * 刪除考核週期
     */
    void delete(PerformanceCycle cycle);

    /**
     * 檢查名稱是否存在
     */
    boolean existsByName(String cycleName);
}
