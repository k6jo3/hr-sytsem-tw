package com.company.hrms.performance.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;

/**
 * 考核記錄 Repository 介面
 */
public interface IPerformanceReviewRepository {

    /**
     * 儲存考核記錄
     */
    PerformanceReview save(PerformanceReview review);

    /**
     * 根據 ID 查詢
     */
    Optional<PerformanceReview> findById(ReviewId reviewId);

    /**
     * 查詢員工的考核記錄
     */
    List<PerformanceReview> findByEmployeeId(UUID employeeId, CycleId cycleId);

    /**
     * 查詢團隊考核記錄（分頁）
     */
    Page<PerformanceReview> findTeamReviews(
            CycleId cycleId,
            List<UUID> employeeIds,
            ReviewStatus status,
            Pageable pageable);

    /**
     * 查詢特定週期和員工的考核記錄
     */
    Optional<PerformanceReview> findByCycleAndEmployeeAndType(
            CycleId cycleId,
            UUID employeeId,
            ReviewType reviewType);

    /**
     * 查詢週期內所有已確認的考核記錄
     */
    List<PerformanceReview> findFinalizedReviewsByCycle(CycleId cycleId);

    /**
     * 檢查是否已存在考核記錄
     */
    boolean existsByCycleAndEmployeeAndType(
            CycleId cycleId,
            UUID employeeId,
            ReviewType reviewType);
}
