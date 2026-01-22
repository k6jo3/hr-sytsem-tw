package com.company.hrms.recruitment.application.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCandidateContext extends PipelineContext {
    // 輸入
    private CreateCandidateRequest request;

    // 輸出 (Domain Model)
    private Candidate candidate;

    // 上下文使用者
    private JWTModel currentUser;
}
