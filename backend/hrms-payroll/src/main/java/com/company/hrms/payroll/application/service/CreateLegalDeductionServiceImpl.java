package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;
import com.company.hrms.payroll.application.factory.LegalDeductionDtoFactory;
import com.company.hrms.payroll.application.service.context.CreateLegalDeductionContext;
import com.company.hrms.payroll.application.service.task.InitLegalDeductionTask;
import com.company.hrms.payroll.application.service.task.SaveLegalDeductionTask;

import lombok.RequiredArgsConstructor;

/**
 * 建立法扣款服務
 * 使用 Pipeline 模式編排建立流程
 */
@Service("createLegalDeductionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateLegalDeductionServiceImpl
        extends AbstractCommandService<CreateLegalDeductionRequest, LegalDeductionResponse> {

    private final InitLegalDeductionTask initLegalDeductionTask;
    private final SaveLegalDeductionTask saveLegalDeductionTask;

    @Override
    protected LegalDeductionResponse doExecute(CreateLegalDeductionRequest request, JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context
        CreateLegalDeductionContext context = new CreateLegalDeductionContext(request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(initLegalDeductionTask)
                .next(saveLegalDeductionTask)
                .execute();

        // 3. 回傳結果
        return LegalDeductionDtoFactory.toResponse(context.getLegalDeduction());
    }
}
