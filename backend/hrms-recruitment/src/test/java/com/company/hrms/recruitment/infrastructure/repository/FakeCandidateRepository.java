package com.company.hrms.recruitment.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ICandidateRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeCandidateRepository repo = new FakeCandidateRepository();
 * repo.seed(Candidate.create(openingId, "張三", "zhang@test.com", ...));
 *
 * List&lt;Candidate&gt; candidates = repo.findByOpeningId(openingId);
 * assertThat(candidates).hasSize(1);
 * </pre>
 */
public class FakeCandidateRepository
        extends InMemoryBaseRepository<Candidate, CandidateId>
        implements ICandidateRepository {

    public FakeCandidateRepository() {
        super(Candidate.class, Candidate::getId);
    }

    /**
     * 新增應徵者（介面回傳實體）
     */
    @Override
    public Candidate save(Candidate candidate) {
        return doSave(candidate);
    }

    /**
     * 更新應徵者（介面回傳實體）
     */
    @Override
    public Candidate update(Candidate candidate) {
        doSave(candidate);
        return candidate;
    }

    /**
     * 刪除應徵者
     */
    @Override
    public void delete(Candidate candidate) {
        super.delete(candidate);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Candidate> findById(CandidateId id) {
        return super.findById(id);
    }

    /**
     * 分頁查詢
     */
    @Override
    public Page<Candidate> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 依職缺查詢應徵者
     */
    @Override
    public List<Candidate> findByOpeningId(OpeningId openingId) {
        return findByField("openingId", openingId);
    }

    /**
     * 依職缺和狀態查詢（看板用）
     * 使用 stream 進行多欄位過濾
     */
    @Override
    public List<Candidate> findByOpeningIdAndStatus(OpeningId openingId, CandidateStatus status) {
        return findAll().stream()
                .filter(c -> openingId.equals(c.getOpeningId()))
                .filter(c -> status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 計算職缺的應徵人數
     */
    @Override
    public long countByOpeningId(OpeningId openingId) {
        return findByOpeningId(openingId).size();
    }

    /**
     * 檢查是否已應徵過同一職缺
     * 使用 stream 進行 email + openingId 聯合查詢
     */
    @Override
    public boolean existsByEmailAndOpeningId(String email, OpeningId openingId) {
        return findAll().stream()
                .anyMatch(c -> email.equals(c.getEmail())
                        && openingId.equals(c.getOpeningId()));
    }

    /**
     * 查詢總數
     */
    @Override
    public long count(QueryGroup query) {
        return super.count(query);
    }
}
