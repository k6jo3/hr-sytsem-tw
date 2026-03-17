package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.LegalDeductionActionRequest;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;
import com.company.hrms.payroll.application.factory.LegalDeductionDtoFactory;
import com.company.hrms.payroll.application.service.context.LegalDeductionActionContext;
import com.company.hrms.payroll.application.service.task.ExecuteLegalDeductionActionTask;
import com.company.hrms.payroll.application.service.task.FetchLegalDeductionTask;

import lombok.RequiredArgsConstructor;

/**
 * 暫停法扣款服務
 */
@Service("suspendLegalDeductionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SuspendLegalDeductionServiceImpl
        extends AbstractCommandService<LegalDeductionActionRequest, LegalDeductionResponse> {

    private final FetchLegalDeductionTask fetchLegalDeductionTask;
    private final ExecuteLegalDeductionActionTask executeLegalDeductionActionTask;

    @Override
    protected LegalDeductionResponse doExecute(LegalDeductionActionRequest request, JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context
        LegalDeductionActionContext context = new LegalDeductionActionContext(request, currentUser, "SUSPEND");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchLegalDeductionTask)
                .next(executeLegalDeductionActionTask)
                .execute();

        // 3. 回傳結果
        return LegalDeductionDtoFactory.toResponse(context.getLegalDeduction());
    }
}
