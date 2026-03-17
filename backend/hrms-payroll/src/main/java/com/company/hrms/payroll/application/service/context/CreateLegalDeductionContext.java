package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 建立法扣款 Pipeline Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateLegalDeductionContext extends PipelineContext {

    // === 輸入 ===
    private final CreateLegalDeductionRequest request;
    private final JWTModel currentUser;

    // === 輸出 ===
    private LegalDeduction legalDeduction;

    public CreateLegalDeductionContext(CreateLegalDeductionRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
