package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.LegalDeductionActionRequest;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 法扣款動作 Pipeline Context
 * 用於暫停、恢復、終止等操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LegalDeductionActionContext extends PipelineContext {

    // === 輸入 ===
    private final LegalDeductionActionRequest request;
    private final JWTModel currentUser;
    private final String actionType; // SUSPEND, RESUME, TERMINATE

    // === 中間資料 ===
    private LegalDeduction legalDeduction;

    public LegalDeductionActionContext(LegalDeductionActionRequest request, JWTModel currentUser, String actionType) {
        this.request = request;
        this.currentUser = currentUser;
        this.actionType = actionType;
    }
}
