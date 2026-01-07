package com.company.hrms.recruitment.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.service.context.HireCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入 Offer Task (Infrastructure)
 */
@Component("loadOfferByCandidateTask")
@RequiredArgsConstructor
public class LoadOfferByCandidateTask implements PipelineTask<HireCandidateContext> {

    private final IOfferRepository offerRepository;

    @Override
    public void execute(HireCandidateContext context) throws Exception {
        Offer offer = offerRepository
                .findByCandidateId(CandidateId.of(context.getCandidateId()))
                .orElse(null); // Offer 可能不存在，後續 Task 會驗證

        context.setOffer(offer);
    }

    @Override
    public String getName() {
        return "載入 Offer";
    }
}
