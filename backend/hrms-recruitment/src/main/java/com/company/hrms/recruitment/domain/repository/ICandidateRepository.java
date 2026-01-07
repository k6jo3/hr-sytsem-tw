package com.company.hrms.recruitment.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 應徵者 Repository 介面
 */
public interface ICandidateRepository {

    /**
     * 儲存應徵者
     */
    Candidate save(Candidate candidate);

    /**
     * 刪除應徵者
     */
    void delete(Candidate candidate);

    /**
     * 根據 ID 查詢
     */
    Optional<Candidate> findById(CandidateId id);

    /**
     * 分頁查詢
     */
    Page<Candidate> findAll(QueryGroup query, Pageable pageable);

    /**
     * 依職缺查詢應徵者
     */
    List<Candidate> findByOpeningId(OpeningId openingId);

    /**
     * 依職缺和狀態查詢（看板用）
     */
    List<Candidate> findByOpeningIdAndStatus(OpeningId openingId, CandidateStatus status);

    /**
     * 計算職缺的應徵人數
     */
    long countByOpeningId(OpeningId openingId);
}
