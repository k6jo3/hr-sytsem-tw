package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.UpdateCandidateStatusContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

@Component
public class SaveUpdatedCandidateTask implements PipelineTask<UpdateCandidateStatusContext> {

    @Autowired
    private ICandidateRepository candidateRepository;

    @Override
    public void execute(UpdateCandidateStatusContext context) {
        Candidate candidate = context.getCandidate();
        candidateRepository.update(candidate);
    }
}
