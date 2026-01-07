package com.company.hrms.recruitment.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;

/**
 * Offer Repository 介面
 */
public interface IOfferRepository {

    /**
     * 儲存 Offer
     */
    Offer save(Offer offer);

    /**
     * 刪除 Offer
     */
    void delete(Offer offer);

    /**
     * 根據 ID 查詢
     */
    Optional<Offer> findById(OfferId id);

    /**
     * 分頁查詢
     */
    Page<Offer> findAll(QueryGroup query, Pageable pageable);

    /**
     * 依應徵者查詢 Offer
     */
    Optional<Offer> findByCandidateId(CandidateId candidateId);
}
