package com.company.hrms.performance.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * IPerformanceReviewRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakePerformanceReviewRepository repo = new FakePerformanceReviewRepository();
 * repo.seed(PerformanceReview.create(cycleId, employeeId, reviewerId, ReviewType.SELF));
 *
 * Optional&lt;PerformanceReview&gt; found = repo.findById(reviewId);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakePerformanceReviewRepository
        extends InMemoryBaseRepository<PerformanceReview, ReviewId>
        implements IPerformanceReviewRepository {

    public FakePerformanceReviewRepository() {
        super(PerformanceReview.class, PerformanceReview::getId);
    }

    /**
     * 儲存考核記錄（介面回傳實體）
     */
    @Override
    public PerformanceReview save(PerformanceReview review) {
        return doSave(review);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<PerformanceReview> findById(ReviewId reviewId) {
        return super.findById(reviewId);
    }

    /**
     * 查詢所有考核記錄（分頁）
     */
    @Override
    public Page<PerformanceReview> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }
}
