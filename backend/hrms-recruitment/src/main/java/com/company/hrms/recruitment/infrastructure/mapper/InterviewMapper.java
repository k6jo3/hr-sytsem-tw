package com.company.hrms.recruitment.infrastructure.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;

@Component
public class InterviewMapper {

    public InterviewResponse toResponse(Interview interview) {
        if (interview == null) {
            return null;
        }

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
