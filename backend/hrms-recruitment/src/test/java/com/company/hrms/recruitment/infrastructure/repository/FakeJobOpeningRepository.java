package com.company.hrms.recruitment.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * IJobOpeningRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeJobOpeningRepository repo = new FakeJobOpeningRepository();
 * repo.seed(JobOpening.create(...));
 *
 * Optional&lt;JobOpening&gt; found = repo.findById(openingId);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeJobOpeningRepository
        extends InMemoryBaseRepository<JobOpening, OpeningId>
        implements IJobOpeningRepository {

    public FakeJobOpeningRepository() {
        super(JobOpening.class, JobOpening::getId);
    }

    /**
     * 新增職缺（介面回傳實體）
     */
    @Override
    public JobOpening save(JobOpening jobOpening) {
        return doSave(jobOpening);
    }

    /**
     * 更新職缺（介面回傳實體）
     */
    @Override
    public JobOpening update(JobOpening jobOpening) {
        doSave(jobOpening);
        return jobOpening;
    }

    /**
     * 刪除職缺
     */
    @Override
    public void delete(JobOpening jobOpening) {
        super.delete(jobOpening);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<JobOpening> findById(OpeningId id) {
        return super.findById(id);
    }

    /**
     * 分頁查詢
     */
    @Override
    public Page<JobOpening> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 查詢總數
     */
    @Override
    public long count(QueryGroup query) {
        return super.count(query);
    }
}
