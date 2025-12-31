package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.application.factory.PayrollRunDtoFactory;
import com.company.hrms.payroll.application.service.context.BankTransferContext;
import com.company.hrms.payroll.application.service.task.FetchPayrollRunWithPayslipsTask;
import com.company.hrms.payroll.application.service.task.GenerateBankFileTask;

import lombok.RequiredArgsConstructor;

/**
 * 產生銀行薪轉檔案服務
 * 使用 Pipeline 模式編排薪轉檔產生流程
 * 
 * 外部服務整合說明：
 * - 實際檔案產生邏輯在 GenerateBankFileTask 中
 * - 需在 hrms-document 模組完成後注入 BankTransferFileGenerator
 */
@Service("generateBankTransferFileServiceImpl")
@Transactional
@RequiredArgsConstructor
public class GenerateBankTransferFileServiceImpl
        extends AbstractCommandService<PayrollRunActionRequest, PayrollRunResponse> {

    private final FetchPayrollRunWithPayslipsTask fetchTask;
    private final GenerateBankFileTask generateTask;

    @Override
    protected PayrollRunResponse doExecute(PayrollRunActionRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        BankTransferContext context = new BankTransferContext(request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchTask)
                .next(generateTask)
                .execute();

        // 3. 回傳結果
        return PayrollRunDtoFactory.toResponse(context.getPayrollRun());
    }
}
