package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.model.valueobject.RecruitmentSource;
import com.company.hrms.recruitment.infrastructure.repository.CandidateRepositoryImpl;

@Component
public class SaveCandidateTask implements PipelineTask<CreateCandidateContext> {

    @Autowired
    private CandidateRepositoryImpl candidateRepository;

    @Override
    public void execute(CreateCandidateContext context) {
        CreateCandidateRequest request = context.getRequest();

        RecruitmentSource source = request.getSource() != null
                ? RecruitmentSource.valueOf(request.getSource())
                : null;

        Candidate candidate = Candidate.create(
                OpeningId.of(request.getOpeningId()),
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                source);

        // 設定其他選填欄位
        candidate.updateResume(request.getResumeUrl(), request.getCoverLetter());
        candidate.updateExpectations(request.getExpectedSalary(), request.getAvailableDate());

        // Note: 處理推薦人 referrerId (需要轉換為 UUID)

        candidateRepository.save(candidate);

        context.setCandidate(candidate);
    }
}
