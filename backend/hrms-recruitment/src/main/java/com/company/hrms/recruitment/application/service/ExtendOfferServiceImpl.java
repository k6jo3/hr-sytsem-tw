package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.dto.offer.ExtendOfferRequest;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;

import lombok.RequiredArgsConstructor;

/**
 * 延長 Offer 到期日 Service
 */
@Service("extendOfferServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ExtendOfferServiceImpl
        implements CommandApiService<ExtendOfferRequest, OfferResponse> {

    private final IOfferRepository offerRepository;

    @Override
    public OfferResponse execCommand(
            ExtendOfferRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        String offerId = args[0];

        Offer offer = offerRepository.findById(OfferId.of(offerId))
                .orElseThrow(() -> new IllegalArgumentException("Offer 不存在：" + offerId));

        // 呼叫 Domain 方法
        offer.extendExpiryDate(request.getNewExpiryDate());

        // 儲存
        Offer saved = offerRepository.save(offer);

        return toResponse(saved);
    }

    private OfferResponse toResponse(Offer offer) {
        return OfferResponse.builder()
                .id(offer.getId().getValue().toString())
                .candidateId(offer.getCandidateId().getValue().toString())
                .candidateName(offer.getCandidateName())
                .offeredPosition(offer.getOfferedPosition())
                .offeredSalary(offer.getOfferedSalary())
                .offeredStartDate(offer.getOfferedStartDate())
                .offerDate(offer.getOfferDate())
                .expiryDate(offer.getExpiryDate())
                .status(offer.getStatus())
                .responseDate(offer.getResponseDate())
                .rejectionReason(offer.getRejectionReason())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }
}
