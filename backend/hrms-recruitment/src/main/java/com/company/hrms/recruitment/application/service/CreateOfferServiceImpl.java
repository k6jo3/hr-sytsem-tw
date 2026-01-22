package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.dto.offer.CreateOfferRequest;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立 Offer Service
 */
@Service("createOfferServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateOfferServiceImpl
                implements CommandApiService<CreateOfferRequest, OfferResponse> {

        private final ICandidateRepository candidateRepository;
        private final IOfferRepository offerRepository;

        @Override
        public OfferResponse execCommand(
                        CreateOfferRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 驗證應徵者存在
                CandidateId candidateId = CandidateId.of(request.getCandidateId());
                Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> new IllegalArgumentException("應徵者不存在：" + request.getCandidateId()));

                // 建立 Offer
                Offer offer = Offer.create(
                                candidateId,
                                candidate.getFullName(),
                                request.getOfferedPosition(),
                                request.getOfferedSalary(),
                                request.getOfferedStartDate(),
                                request.getExpiryDate());

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
