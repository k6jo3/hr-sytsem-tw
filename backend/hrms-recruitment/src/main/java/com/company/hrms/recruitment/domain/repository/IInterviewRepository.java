package com.company.hrms.recruitment.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;

/**
 * 面試 Repository 介面
 */
public interface IInterviewRepository {

    /**
     * 儲存面試
     */
    Interview save(Interview interview);

    /**
     * 刪除面試
     */
    void delete(Interview interview);

    /**
     * 根據 ID 查詢
     */
    Optional<Interview> findById(InterviewId id);

    /**
     * 分頁查詢
     */
    Page<Interview> findAll(QueryGroup query, Pageable pageable);

    /**
     * 依應徵者查詢面試
     */
    List<Interview> findByCandidateId(CandidateId candidateId);
}
