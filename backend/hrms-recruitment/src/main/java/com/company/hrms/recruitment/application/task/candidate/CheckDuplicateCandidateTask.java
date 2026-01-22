package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

@Component
public class CheckDuplicateCandidateTask implements PipelineTask<CreateCandidateContext> {

    @Autowired
    private ICandidateRepository candidateRepository;

    @Override
    public void execute(CreateCandidateContext context) {
        CreateCandidateRequest request = context.getRequest();

        boolean exists = candidateRepository.existsByEmailAndOpeningId(
                request.getEmail(),
                OpeningId.of(request.getOpeningId()));

        if (exists) {
            throw new IllegalArgumentException("該 Email 已投遞過此職缺");
        }
    }
}
