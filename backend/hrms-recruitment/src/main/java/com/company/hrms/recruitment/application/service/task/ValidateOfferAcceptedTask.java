package com.company.hrms.recruitment.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.service.context.HireCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

/**
 * 驗證 Offer 狀態 Task (Domain Logic)
 */
@Component("validateOfferAcceptedTask")
public class ValidateOfferAcceptedTask implements PipelineTask<HireCandidateContext> {

    @Override
    public void execute(HireCandidateContext context) throws Exception {
        Offer offer = context.getOffer();

        if (offer == null) {
            throw new IllegalStateException("該應徵者尚未收到 Offer，無法錄取");
        }

        if (offer.getStatus() != OfferStatus.ACCEPTED) {
            throw new IllegalStateException("Offer 尚未被接受，無法錄取 (當前狀態: " + offer.getStatus() + ")");
        }
    }

    @Override
    public String getName() {
        return "驗證 Offer 狀態";
    }
}
