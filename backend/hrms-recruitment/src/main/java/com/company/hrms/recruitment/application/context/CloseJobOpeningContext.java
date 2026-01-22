package com.company.hrms.recruitment.application.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningRequest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CloseJobOpeningContext extends PipelineContext {
    // 輸入
    private String openingId;
    private CloseJobOpeningRequest request;

    // 輸出
    private JobOpening jobOpening;

    // 上下文使用者
    private JWTModel currentUser;
}
