package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.UpdateCandidateStatusContext; // Reuse context or create specific? reusing for now as it has candidate
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

@Component
public class HireCandidateTask implements PipelineTask<UpdateCandidateStatusContext> {

    @Override
    public void execute(UpdateCandidateStatusContext context) {
        Candidate candidate = context.getCandidate();
        candidate.hire();
    }
}
