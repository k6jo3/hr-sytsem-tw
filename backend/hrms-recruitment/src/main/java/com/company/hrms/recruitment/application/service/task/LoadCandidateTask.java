package com.company.hrms.recruitment.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.service.context.HireCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入應徵者 Task (Infrastructure)
 */
@Component("loadCandidateTask")
@RequiredArgsConstructor
public class LoadCandidateTask implements PipelineTask<HireCandidateContext> {

    private final ICandidateRepository candidateRepository;

    @Override
    public void execute(HireCandidateContext context) throws Exception {
        Candidate candidate = candidateRepository
                .findById(CandidateId.of(context.getCandidateId()))
                .orElseThrow(() -> new IllegalArgumentException("應徵者不存在: " + context.getCandidateId()));

        context.setCandidate(candidate);
    }

    @Override
    public String getName() {
        return "載入應徵者";
    }
}
