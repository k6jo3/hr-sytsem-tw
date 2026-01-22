package com.company.hrms.recruitment.application.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateJobOpeningContext extends PipelineContext {
    // 輸入
    private CreateJobOpeningRequest request;

    // 輸出 (Domain Model)
    private JobOpening jobOpening;

    // 中間產物
    private String departmentName; // 例如：從組織服務獲取

    // 上下文使用者
    private JWTModel currentUser;
}
