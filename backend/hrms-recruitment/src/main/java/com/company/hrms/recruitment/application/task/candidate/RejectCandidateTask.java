package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.UpdateCandidateStatusContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

@Component
public class RejectCandidateTask implements PipelineTask<UpdateCandidateStatusContext> {

    @Override
    public void execute(UpdateCandidateStatusContext context) {
        Candidate candidate = context.getCandidate();
        String reason = context.getRequest().getRejectionReason();

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("拒絕必須提供原因");
        }

        candidate.reject(reason);
    }
}
