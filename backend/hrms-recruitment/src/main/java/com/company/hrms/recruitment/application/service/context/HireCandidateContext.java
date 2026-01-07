package com.company.hrms.recruitment.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;

import lombok.Getter;
import lombok.Setter;

/**
 * 錄取應徵者 Pipeline Context
 */
@Getter
@Setter
public class HireCandidateContext extends PipelineContext {

    // === 輸入 ===
    /**
     * 應徵者 ID
     */
    private final String candidateId;

    // === 中間資料 ===
    /**
     * 載入的應徵者
     */
    private Candidate candidate;

    /**
     * 載入的 Offer
     */
    private Offer offer;

    /**
     * 建構子
     */
    public HireCandidateContext(String candidateId) {
        this.candidateId = candidateId;
    }
}
