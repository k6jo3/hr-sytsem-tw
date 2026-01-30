package com.company.hrms.recruitment.application.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.SubmitEvaluationRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 提交面試評估 Service
 */
@Service("submitEvaluationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SubmitEvaluationServiceImpl
                implements CommandApiService<SubmitEvaluationRequest, InterviewResponse> {

        private final IInterviewRepository interviewRepository;

        @Override
        public InterviewResponse execCommand(
                        SubmitEvaluationRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String interviewId = args[0];

                Interview interview = interviewRepository.findById(InterviewId.of(interviewId))
                                .orElseThrow(() -> new IllegalArgumentException("面試不存在：" + interviewId));

                // 呼叫 Domain 方法
                interview.addEvaluation(
                                request.getInterviewerId(),
                                request.getTechnicalScore(),
                                request.getCommunicationScore(),
                                request.getCultureFitScore(),
                                request.getOverallRating(),
                                request.getComments(),
                                request.getStrengths(),
                                request.getConcerns());

                // 儲存
                Interview saved = interviewRepository.save(interview);

                return toResponse(saved);
        }

        private InterviewResponse toResponse(Interview interview) {
                // TODO: 程式過長，縮排太多，建議改用objectMapper或structMapper
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
