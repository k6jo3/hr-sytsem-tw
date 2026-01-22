package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢 Offer 詳情 Service
 */
@Service("getOfferServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetOfferServiceImpl
        implements QueryApiService<Object, OfferResponse> {

    private final IOfferRepository offerRepository;

    @Override
    public OfferResponse getResponse(
            Object request,
            JWTModel currentUser,
            String... args) throws Exception {

        String offerId = args[0];

        Offer offer = offerRepository.findById(OfferId.of(offerId))
                .orElseThrow(() -> new IllegalArgumentException("Offer 不存在：" + offerId));

        return toResponse(offer);
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
