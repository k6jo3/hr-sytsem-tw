package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.application.dto.candidate.CandidateSearchDto;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

@Service("getCandidatesServiceImpl")
public class GetCandidatesServiceImpl implements QueryApiService<CandidateSearchDto, Object> {

    @Autowired
    private ICandidateRepository candidateRepository;

    @Override
    public Object getResponse(CandidateSearchDto searchDto, JWTModel currentUser, String... args) throws Exception {
        QueryGroup query = QueryBuilder.fromCondition(searchDto);
        Pageable pageable = searchDto.toPageable();

        Page<Candidate> page = candidateRepository.findAll(query, pageable);

        return page.map(this::toResponse);
    }

    private CandidateResponse toResponse(Candidate candidate) {
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
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}
