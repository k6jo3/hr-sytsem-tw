package com.company.hrms.recruitment.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.application.dto.offer.OfferSearchDto;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢 Offer 列表 Service
 */
@Service("getOffersServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetOffersServiceImpl
        implements QueryApiService<OfferSearchDto, Page<OfferResponse>> {

    private final IOfferRepository offerRepository;

    @Override
    public Page<OfferResponse> getResponse(
            OfferSearchDto request,
            JWTModel currentUser,
            String... args) throws Exception {

        QueryGroup query = QueryBuilder.fromCondition(request);
        Pageable pageable = request;

        Page<Offer> page = offerRepository.findAll(query, pageable);

        return page.map(this::toResponse);
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
