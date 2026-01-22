package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.UpdateCandidateStatusContext;
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.application.dto.candidate.UpdateCandidateStatusRequest;
import com.company.hrms.recruitment.application.task.candidate.LoadCandidateTask;
import com.company.hrms.recruitment.application.task.candidate.RejectCandidateTask;
import com.company.hrms.recruitment.application.task.candidate.SaveUpdatedCandidateTask;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

@Service("rejectCandidateServiceImpl")
public class RejectCandidateServiceImpl
                implements CommandApiService<UpdateCandidateStatusRequest, CandidateResponse> {

        @Autowired
        private LoadCandidateTask loadTask;

        @Autowired
        private RejectCandidateTask rejectTask;

        @Autowired
        private SaveUpdatedCandidateTask saveTask;

        @Override
        @Transactional
        public CandidateResponse execCommand(UpdateCandidateStatusRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                String candidateId = args[0];

                UpdateCandidateStatusContext ctx = new UpdateCandidateStatusContext();
                ctx.setCandidateId(candidateId);
                ctx.setRequest(request);
                ctx.setCurrentUser(currentUser);

                BusinessPipeline.start(ctx)
                                .next(loadTask)
                                .next(rejectTask)
                                .next(saveTask)
                                .execute();

                Candidate candidate = ctx.getCandidate();

                return CandidateResponse.builder()
                                .candidateId(candidate.getId().getValue().toString())
                                .openingId(candidate.getOpeningId().getValue().toString())
                                .fullName(candidate.getFullName())
                                .email(candidate.getEmail())
                                .phoneNumber(candidate.getPhoneNumber())
                                .status(candidate.getStatus().name())
                                .rejectionReason(candidate.getRejectionReason())
                                .createdAt(candidate.getCreatedAt())
                                .updatedAt(candidate.getUpdatedAt())
                                .build();
        }
}
