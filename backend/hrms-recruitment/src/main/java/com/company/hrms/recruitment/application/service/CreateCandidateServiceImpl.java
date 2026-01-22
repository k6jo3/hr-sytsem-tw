package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.application.task.candidate.CheckDuplicateCandidateTask;
import com.company.hrms.recruitment.application.task.candidate.PublishCandidateEventTask;
import com.company.hrms.recruitment.application.task.candidate.SaveCandidateTask;
import com.company.hrms.recruitment.application.task.candidate.ValidateCandidateTask;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

@Service("createCandidateServiceImpl")
public class CreateCandidateServiceImpl
                implements CommandApiService<CreateCandidateRequest, CandidateResponse> {

        @Autowired
        private ValidateCandidateTask validateTask;

        @Autowired
        private CheckDuplicateCandidateTask checkDuplicateTask;

        @Autowired
        private SaveCandidateTask saveTask;

        @Autowired
        private PublishCandidateEventTask eventTask;

        @Override
        @Transactional
        public CandidateResponse execCommand(CreateCandidateRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                CreateCandidateContext ctx = new CreateCandidateContext();
                ctx.setRequest(request);
                ctx.setCurrentUser(currentUser);

                BusinessPipeline.start(ctx)
                                .next(validateTask)
                                .next(checkDuplicateTask)
                                .next(saveTask)
                                .next(eventTask)
                                .execute();

                Candidate candidate = ctx.getCandidate();

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
                                // .referrerId(candidate.getReferrerId() != null ?
                                // candidate.getReferrerId().toString() : null)
                                .createdAt(candidate.getCreatedAt())
                                .updatedAt(candidate.getUpdatedAt())
                                .build();
        }
}
