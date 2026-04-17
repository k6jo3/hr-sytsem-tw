package com.company.hrms.recruitment.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * IOfferRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeOfferRepository repo = new FakeOfferRepository();
 * repo.seed(Offer.create(candidateId, ...));
 *
 * Optional&lt;Offer&gt; found = repo.findByCandidateId(candidateId);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeOfferRepository
        extends InMemoryBaseRepository<Offer, OfferId>
        implements IOfferRepository {

    public FakeOfferRepository() {
        super(Offer.class, Offer::getId);
    }

    /**
     * 儲存 Offer（介面回傳實體）
     */
    @Override
    public Offer save(Offer offer) {
        return doSave(offer);
    }

    /**
     * 刪除 Offer
     */
    @Override
    public void delete(Offer offer) {
        super.delete(offer);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Offer> findById(OfferId id) {
        return super.findById(id);
    }

    /**
     * 分頁查詢
     */
    @Override
    public Page<Offer> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 依應徵者查詢 Offer
     */
    @Override
    public Optional<Offer> findByCandidateId(CandidateId candidateId) {
        return findOneByField("candidateId", candidateId);
    }
}
