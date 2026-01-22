package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

@Service("getCandidateDetailServiceImpl")
public class GetCandidateDetailServiceImpl implements QueryApiService<Object, CandidateResponse> {

    @Autowired
    private ICandidateRepository candidateRepository;

    @Override
    public CandidateResponse getResponse(Object request, JWTModel currentUser, String... args) throws Exception {
        String candidateId = args[0];

        Candidate candidate = candidateRepository.findById(CandidateId.of(candidateId))
                .orElseThrow(() -> new IllegalArgumentException("找不到應徵者 ID: " + candidateId));

        return CandidateResponse.builder()
                .candidateId(candidate.getId().getValue().toString())
                .openingId(candidate.getOpeningId().getValue().toString())
                .fullName(candidate.getFullName())
                .email(candidate.getEmail())
                .phoneNumber(candidate.getPhoneNumber())
                .status(candidate.getStatus().name())
                .source(candidate.getSource() != null ? candidate.getSource().name() : null)
                .applicationDate(candidate.getApplicationDate())
                .resumeUrl(candidate.getResumeUrl())
                .rejectionReason(candidate.getRejectionReason())
                .coverLetter(candidate.getCoverLetter())
                .expectedSalary(candidate.getExpectedSalary())
                .availableDate(candidate.getAvailableDate())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}
