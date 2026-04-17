package com.company.hrms.performance.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * IPerformanceCycleRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakePerformanceCycleRepository repo = new FakePerformanceCycleRepository();
 * repo.seed(PerformanceCycle.create("2026年度考核", CycleType.ANNUAL, ...));
 *
 * Optional&lt;PerformanceCycle&gt; found = repo.findById(cycleId);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakePerformanceCycleRepository
        extends InMemoryBaseRepository<PerformanceCycle, CycleId>
        implements IPerformanceCycleRepository {

    public FakePerformanceCycleRepository() {
        super(PerformanceCycle.class, PerformanceCycle::getId);
    }

    /**
     * 儲存考核週期（介面回傳實體）
     */
    @Override
    public PerformanceCycle save(PerformanceCycle cycle) {
        return doSave(cycle);
    }

    /**
     * 刪除考核週期
     */
    @Override
    public void delete(PerformanceCycle cycle) {
        super.delete(cycle);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<PerformanceCycle> findById(CycleId cycleId) {
        return super.findById(cycleId);
    }

    /**
     * 查詢所有考核週期（分頁）
     */
    @Override
    public Page<PerformanceCycle> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }
}
