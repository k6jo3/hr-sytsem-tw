package com.company.hrms.recruitment.application.service;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.InterviewSearchDto;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢面試列表 Service
 */
@Service("getInterviewsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetInterviewsServiceImpl
                implements QueryApiService<InterviewSearchDto, Page<InterviewResponse>> {

        private final IInterviewRepository interviewRepository;

        @Override
        public Page<InterviewResponse> getResponse(
                        InterviewSearchDto request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 從 DTO 取得 QueryGroup
                QueryGroup query = QueryBuilder.fromCondition(request);
                Pageable pageable = request;

                // 查詢
                Page<Interview> page = interviewRepository.findAll(query, pageable);

                // 轉換為 Response
                return page.map(this::toResponse);
        }

        private InterviewResponse toResponse(Interview interview) {
                // TODO: 程式過長，縮排太多，建立改用objectMapper或structMapper
                return InterviewResponse.builder()
                                .id(interview.getId().getValue().toString())
                                .candidateId(interview.getCandidateId().getValue().toString())
                                .candidateName(interview.getCandidateName())
                                .interviewRound(interview.getInterviewRound())
                                .interviewType(interview.getInterviewType())
                                .interviewDate(interview.getInterviewDate())
                                .location(interview.getLocation())
                                .interviewerIds(interview.getInterviewerIds())
                                .status(interview.getStatus())
                                .evaluations(interview.getEvaluations().stream()
                                                .map(e -> InterviewResponse.EvaluationDto.builder()
                                                                .interviewerId(e.getInterviewerId())
                                                                .technicalScore(e.getTechnicalScore())
                                                                .communicationScore(e.getCommunicationScore())
                                                                .cultureFitScore(e.getCultureFitScore())
                                                                .overallRating(e.getOverallRating() != null
                                                                                ? e.getOverallRating().name()
                                                                                : null)
                                                                .comments(e.getComments())
                                                                .strengths(e.getStrengths())
                                                                .concerns(e.getConcerns())
                                                                .evaluatedAt(e.getEvaluatedAt())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .createdAt(interview.getCreatedAt())
                                .updatedAt(interview.getUpdatedAt())
                                .build();
        }
}
