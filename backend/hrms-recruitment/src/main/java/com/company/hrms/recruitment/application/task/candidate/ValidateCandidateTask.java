package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.domain.model.valueobject.RecruitmentSource;

@Component
public class ValidateCandidateTask implements PipelineTask<CreateCandidateContext> {

    @Override
    public void execute(CreateCandidateContext context) {
        CreateCandidateRequest request = context.getRequest();

        if (request.getOpeningId() == null || request.getOpeningId().isBlank()) {
            throw new IllegalArgumentException("職缺 ID 為必填");
        }

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("姓名為必填");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email 為必填");
        }

        // 驗證來源 enum
        if (request.getSource() != null) {
            try {
                RecruitmentSource.valueOf(request.getSource());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("無效的履歷來源: " + request.getSource());
            }
        }
    }
}
