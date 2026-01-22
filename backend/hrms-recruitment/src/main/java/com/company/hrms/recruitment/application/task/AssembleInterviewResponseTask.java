package com.company.hrms.recruitment.application.task;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.ScheduleInterviewContext;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;

/**
 * 組裝面試回應 Task
 */
@Component
public class AssembleInterviewResponseTask implements PipelineTask<ScheduleInterviewContext> {

        @Override
        public void execute(ScheduleInterviewContext ctx) {
                Interview interview = ctx.getInterview();

                InterviewResponse response = InterviewResponse.builder()
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

                ctx.setResponse(response);
        }
}
