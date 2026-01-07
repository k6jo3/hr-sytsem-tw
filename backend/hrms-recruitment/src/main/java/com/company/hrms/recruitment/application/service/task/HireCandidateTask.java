package com.company.hrms.recruitment.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.service.context.HireCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 錄取應徵者 Task (Domain + Infrastructure)
 */
@Component("hireCandidateTask")
@RequiredArgsConstructor
public class HireCandidateTask implements PipelineTask<HireCandidateContext> {

    private final ICandidateRepository candidateRepository;

    @Override
    public void execute(HireCandidateContext context) throws Exception {
        Candidate candidate = context.getCandidate();

        // 執行領域邏輯：錄取
        candidate.hire();

        // 儲存並發布事件
        candidateRepository.save(candidate);
    }

    @Override
    public String getName() {
        return "執行錄取並儲存";
    }
}
