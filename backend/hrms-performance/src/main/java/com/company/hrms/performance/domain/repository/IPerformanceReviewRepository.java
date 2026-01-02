package com.company.hrms.performance.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;

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
         * 查詢所有考核記錄（分頁）
         */
        Page<PerformanceReview> findAll(QueryGroup query, Pageable pageable);
}
